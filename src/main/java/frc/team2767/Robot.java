package frc.team2767;

import com.ctre.phoenix.CANifier.PWMChannel;
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
    logger.info("robotInit");
    COMPONENT.talonProvisioner().enableTimeout(true);
    controls = COMPONENT.controls();
    swerve = COMPONENT.swerveDrive();
    TelemetryService telemetryService = COMPONENT.telemetryService();
    swerve.registerWith(telemetryService);
    telemetryService.register(new UltrasonicRangefinderItem(0, PWMChannel.PWMChannel0));
    telemetryService.register(new UltrasonicRangefinderItem(0, PWMChannel.PWMChannel1));
    telemetryService.start();
    swerve.zeroAzimuthEncoders();
  }

  @Override
  public void teleopInit() {
    logger.info("teleopInit - disabling Talon config timeout and stopping swerve");
    COMPONENT.talonProvisioner().enableTimeout(false);
    COMPONENT.driveSubsystem();
    swerve.stop();
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
      COMPONENT.driveSubsystem().alignWheels();
    }
  }

  @Override
  public void robotPeriodic() {}
}
