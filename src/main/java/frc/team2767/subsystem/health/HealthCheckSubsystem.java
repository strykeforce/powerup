package frc.team2767.subsystem.health;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.moandjiezana.toml.Toml;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Subsystem;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.talon.Talons;

@Singleton
public class HealthCheckSubsystem extends Subsystem implements Runnable {

  static final Logger logger = LoggerFactory.getLogger(HealthCheckSubsystem.class);

  private final int kIterations;
  private final double kRunTimeSec;
  private final double kWarmUpSec;

  private final List<Test> tests;
  private final Talons talonFactory;

  private Thread thread;
  private volatile boolean cancelled;
  private volatile boolean running;

  @Inject
  public HealthCheckSubsystem(Talons talons) {
    talonFactory = talons;
    InputStream in = getClass().getResourceAsStream("/META-INF/powerup/healthcheck.toml");
    Toml toml = new Toml().read(in);
    List<Toml> tomlList = toml.getTables("TEST");
    tests = tomlList.stream().map(t -> t.to(Test.class)).collect(Collectors.toList());
    kIterations = toml.getLong("iterations").intValue();
    kWarmUpSec = toml.getDouble("warmUpSec");
    kRunTimeSec = toml.getDouble("runTimeSec");
  }

  public void initialize() {
    cancelled = false;
    thread = new Thread(this);
    thread.start();
    logger.debug("started thread = {}", thread.getName());
  }

  @Override
  public void run() {
    running = true;
    Iterator<Test> tests = this.tests.iterator();
    while (!cancelled && tests.hasNext()) {
      Test test = tests.next();
      performTest(test);
      test.log(logger);
    }
    if (!cancelled) {
      HtmlReport htmlReport = new HtmlReport(this.tests);
      htmlReport.save();
      CsvReport csvReport = new CsvReport(this.tests);
      csvReport.save();
    }
    running = false;
  }

  private void performTest(Test test) {
    logger.debug("starting {} test", test.name);
    switch (test.type) {
      case DRIVE:
      case AZIMUTH:
      case CLIMBER:
      case INTAKE:
        performFreeSpeedTest(test);
        break;
      case LIFT:
        break;
      case SHOULDER:
        break;
      case INTAKE_RELEASE:
        break;
    }
    logger.debug("completed {} test", test.name);
  }

  private void performFreeSpeedTest(Test test) {
    logger.info("running {}", test.name);
    List<TalonSRX> talons =
        test.ids.stream().map(talonFactory::getTalon).collect(Collectors.toList());

    for (TestCase tc : test.testCases) {
      tc.test = test;
      tc.results = new ArrayList<>();
      for (TalonSRX talon : talons) {
        if (isCanceled(talon)) return;
        Result result = new Result(talon.getDeviceID(), tc);
        tc.results.add(result);

        logger.debug("setting talon {} to output {}", talon.getDeviceID(), tc.output);
        talon.set(ControlMode.PercentOutput, tc.output);

        // come up to speed
        long start = System.nanoTime();
        while (System.nanoTime() - start < kWarmUpSec * 1e9) {
          Timer.delay(0.005);
          if (isCanceled(talon)) return;
        }

        // run test
        for (int i = 0; i < kIterations; i++) {
          if (isCanceled(talon)) return;
          result.velocity += talon.getSelectedSensorVelocity(0);
          result.current += talon.getOutputCurrent();
          Timer.delay(kRunTimeSec / kIterations);
        }
        talon.set(ControlMode.PercentOutput, 0);
        result.velocity /= kIterations;
        result.current /= kIterations;
      }
    }
  }

  private boolean isCanceled(TalonSRX talon) {
    if (cancelled) {
      talon.set(ControlMode.PercentOutput, 0);
      return true;
    }
    return false;
  }

  public boolean isFinished() {
    return !running;
  }

  public void cancel() {
    cancelled = true;
    logger.info("healthcheck cancelled");
  }

  public void end() {
    try {
      thread.join();
    } catch (InterruptedException e) {
      logger.error("thread join", e);
    }
    logger.info("healthcheck ended");
  }

  @Override
  protected void initDefaultCommand() {}
}
