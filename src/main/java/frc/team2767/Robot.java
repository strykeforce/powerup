package frc.team2767;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.TimedRobot;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.swerve.SwerveDrive;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;

public class Robot extends TimedRobot {

  private static final Logger logger = LoggerFactory.getLogger(Robot.class);
  private static final File CONFIG_FILE = new File("/home/lvuser/powerup.toml");

  private SingletonComponent component;
  private Controls controls;
  private final Trigger gyroResetButton =
      new Trigger() {
        @Override
        public boolean get() {
          return controls.getResetButton();
        }

        @Override
        public String toString() {
          return "gyro reset button";
        }
      };
  private final Trigger alignWheelsButton =
      new Trigger() {
        @Override
        public boolean get() {
          return controls.getGamepadBackButton() && controls.getGamepadStartButton();
        }

        @Override
        public String toString() {
          return "wheel alignment button combination";
        }
      };
  private SwerveDrive swerve;

  @Override
  public void robotInit() {
    logger.info("Robot is initializing");
    controls = getComponent().controls();
    swerve = getComponent().swerveDrive();
    TelemetryService telemetryService = getComponent().telemetryService();
    swerve.registerWith(telemetryService);
    telemetryService.start();
    swerve.zeroAzimuthEncoders();
  }

  @Override
  public void teleopInit() {
    logger.info("Robot is enabled in tele-op");
    swerve.stop();
  }

  @Override
  public void teleopPeriodic() {
    if (gyroResetButton.hasActivated()) {
      String msg = "Resetting gyro yaw zero";
      logger.warn(msg);
      DriverStation.reportWarning(msg, false);
      swerve.getGyro().zeroYaw();
    }
    double forward = applyDeadband(controls.getForward());
    double strafe = applyDeadband(controls.getStrafe());
    double azimuth = applyDeadband(controls.getAzimuth());

    swerve.drive(forward, strafe, azimuth);
  }

  @Override
  public void disabledInit() {
    logger.info("Robot is disabled");
  }

  @Override
  public void disabledPeriodic() {
    if (alignWheelsButton.hasActivated()) {
      swerve.saveAzimuthPositions();
      swerve.zeroAzimuthEncoders();
      String msg = "drive wheels were re-aligned";
      logger.info(msg);
      DriverStation.reportWarning(msg, false);
    }
  }

  private double applyDeadband(double input) {
    if (Math.abs(input) < 0.05) {
      return 0;
    }
    return input;
  }

  private SingletonComponent getComponent() {
    if (component == null) {
      logger.info("loading robot configuration from {}", CONFIG_FILE);
      component = DaggerSingletonComponent.builder().config(CONFIG_FILE).build();
    }
    return component;
  }
}
