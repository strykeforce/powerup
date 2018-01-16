package frc.team2767;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.swerve.SwerveDrive;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;

public class Robot extends TimedRobot {

  public static final SingletonComponent COMPONENT;
  private static final File CONFIG_FILE = new File("/home/lvuser/powerup.toml");
  private static final Logger logger = LoggerFactory.getLogger(Robot.class);

  static {
    logger.info("loading robot configuration from {}", CONFIG_FILE);
    COMPONENT = DaggerSingletonComponent.builder().config(CONFIG_FILE).build();
  }

  private SingletonComponent component;
  private Controls controls;
  private final Trigger alignWheelsButton =
      new Trigger() {
        @Override
        public boolean get() {
          return controls.getGamepadBackButton() && controls.getGamepadStartButton();
        }
      };
  private SwerveDrive swerve;

  @Override
  public void robotInit() {
    logger.info("Robot is initializing");
    controls = COMPONENT.controls();
    swerve = COMPONENT.swerveDrive();
    TelemetryService telemetryService = COMPONENT.telemetryService();
    swerve.registerWith(telemetryService);
    telemetryService.start();
    swerve.zeroAzimuthEncoders();

    // commands
    //    alignWheelsButton.whenActive(new AlignWheelsCommand());
  }

  @Override
  public void teleopInit() {
    logger.info("Robot is enabled in tele-op");
    swerve.stop();
  }

  @Override
  public void teleopPeriodic() {
    Scheduler.getInstance().run();
  }

  @Override
  public void disabledInit() {
    logger.info("Robot is disabled");
  }

  @Override
  public void disabledPeriodic() {
    if (alignWheelsButton.hasActivated()) {
      COMPONENT.driveSubsystem().alignWheels();
    }
  }
}
