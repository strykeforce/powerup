package frc.team2767;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import frc.team2767.trigger.Trigger;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;

public class Robot extends TimedRobot {

  public static final SingletonComponent INJECTOR;
  public static final File CONFIG_FILE = new File("/home/lvuser/powerup.toml");
  public static final String TABLE = "POWERUP";
  private static final Logger logger = LoggerFactory.getLogger(Robot.class);

  static {
    logger.info("loading robot configuration from {}", CONFIG_FILE);
    INJECTOR = DaggerSingletonComponent.builder().config(CONFIG_FILE).build();
  }

  private Trigger alignWheelsButton;

  @Override
  public void robotInit() {
    logger.info(INJECTOR.settings().getTable(TABLE).getString("description"));
    alignWheelsButton = INJECTOR.alignWheelsTrigger();
    TelemetryService telemetryService = INJECTOR.telemetryService();
    INJECTOR.graphables().forEach(g -> g.register(telemetryService));
    INJECTOR.driveSubsystem().zeroAzimuthEncoders();
    LiveWindow.disableAllTelemetry();
    telemetryService.start();
  }

  @Override
  public void teleopInit() {
    logger.info("teleopInit - stopping swerve");
    INJECTOR.driveSubsystem().stop();
  }

  @Override
  public void teleopPeriodic() {
    Scheduler.getInstance().run();
  }

  @Override
  public void disabledInit() {
    logger.info("disabledInit");
  }

  @Override
  public void disabledPeriodic() {
    if (alignWheelsButton.hasActivated()) {
      INJECTOR.driveSubsystem().alignWheels();
    }
  }

  @Override
  public void robotPeriodic() {}
}
