package frc.team2767;

import com.ctre.phoenix.CANifier.PWMChannel;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import java.io.File;
import openrio.powerup.MatchData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.swerve.SwerveDrive;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;

public class Robot extends TimedRobot {

  public static final SingletonComponent COMPONENT;
  private static final File CONFIG_FILE = new File("/home/lvuser/powerup.toml");
  private static final String TABLE = "POWERUP";
  private static final Logger logger = LoggerFactory.getLogger(Robot.class);
  private static MatchData.OwnedSide NEAR_SWITCH;
  private static MatchData.OwnedSide FAR_SWITCH;
  private static MatchData.OwnedSide SCALE;

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
  public void autonomousInit() {
    NEAR_SWITCH = MatchData.getOwnedSide(MatchData.GameFeature.SWITCH_NEAR);
    FAR_SWITCH = MatchData.getOwnedSide(MatchData.GameFeature.SWITCH_FAR);
    SCALE = MatchData.getOwnedSide(MatchData.GameFeature.SCALE);
  }

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
    logger.info(COMPONENT.settings().getTable(TABLE).getString("description"));
    LiveWindow.disableAllTelemetry();
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

  public static MatchData.OwnedSide getScale() {
    return SCALE;
  }

  public static MatchData.OwnedSide getNearSwitch() {
    return NEAR_SWITCH;
  }

  public static MatchData.OwnedSide getFarSwitch() {
    return FAR_SWITCH;
  }
}
