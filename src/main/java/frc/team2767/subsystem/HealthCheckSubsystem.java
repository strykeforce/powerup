package frc.team2767.subsystem;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.moandjiezana.toml.Toml;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Subsystem;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.talon.Talons;

@Singleton
public class HealthCheckSubsystem extends Subsystem implements Runnable {

  private static final Logger logger = LoggerFactory.getLogger(HealthCheckSubsystem.class);

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
    InputStream in = getClass().getResourceAsStream("/META-INF/powerup/healthchecks.toml");
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
      outputTest(test);
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

    Map<Integer, List<Result>> results = new HashMap<>();
    test.results = results;

    for (TalonSRX talon : talons) {
      results.put(talon.getDeviceID(), new ArrayList<>());
      for (double output : test.percentOutputs) {
        if (checkCanceled(talon)) return;
        Result result = new Result();
        results.get(talon.getDeviceID()).add(result);

        logger.debug("setting talon {} to output {}", talon.getDeviceID(), output);
        talon.set(ControlMode.PercentOutput, output);
        result.output = output;

        // come up to speed
        long start = System.nanoTime();
        while (System.nanoTime() - start < kWarmUpSec * 1e9) {
          Timer.delay(0.005);
          if (checkCanceled(talon)) return;
        }

        for (int i = 0; i < kIterations; i++) {
          if (checkCanceled(talon)) return;
          result.velocity += talon.getSelectedSensorVelocity(0);
          result.current += talon.getOutputCurrent();
          Timer.delay(kRunTimeSec / kIterations);
        }
        result.velocity /= kIterations;
        result.current /= kIterations;
      }
      talon.set(ControlMode.PercentOutput, 0);
    }
  }

  private boolean checkCanceled(TalonSRX talon) {
    if (cancelled) {
      talon.set(ControlMode.PercentOutput, 0);
      return true;
    }
    return false;
  }

  private void outputTest(Test test) {
    logger.info(test.name);
    logger.info(String.format("%2s  %4s   %4s   %6s", "id", "volt", "curr", "speed"));
    for (int id : test.results.keySet()) {
      for (Result result : test.results.get(id)) {
        logger.info(
            String.format(
                "%2d  %4.1f   %4.2f   %6d",
                id, result.output * 12, result.current, result.velocity));
      }
    }
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

  enum TestType {
    DRIVE,
    AZIMUTH,
    CLIMBER,
    LIFT,
    SHOULDER,
    INTAKE,
    INTAKE_RELEASE,
  }

  static class Test {
    String name;
    TestType type;
    List<Integer> ids;
    List<Double> percentOutputs;
    Range current;
    Range speed;
    Map<Integer, List<Result>> results;
  }

  static class Range {
    double low, high;

    Range(double low, double high) {
      this.low = low;
      this.high = high;
    }

    boolean inRange(double val) {
      return val >= low && val <= high;
    }
  }

  static class Result {
    double output;
    double current;
    int velocity;
  }
}
