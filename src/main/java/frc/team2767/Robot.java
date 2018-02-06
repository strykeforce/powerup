package frc.team2767;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import frc.team2767.control.Trigger;
import frc.team2767.subsystem.DriveSubsystem;
import java.io.File;
import openrio.powerup.MatchData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;

public class Robot extends TimedRobot {

  public static final SingletonComponent INJECTOR;
  public static final File CONFIG_FILE = new File("/home/lvuser/powerup.toml");
  public static final String TABLE = "POWERUP";
  private static final Logger logger = LoggerFactory.getLogger(Robot.class);
  private static MatchData.OwnedSide NEAR_SWITCH;
  private static MatchData.OwnedSide FAR_SWITCH;
  private static MatchData.OwnedSide SCALE;

  static {
    logger.info("loading robot configuration from {}", CONFIG_FILE);
    INJECTOR = DaggerSingletonComponent.builder().config(CONFIG_FILE).build();
  }

  private Settings settings;
  private DriveSubsystem driveSubsystem;
  private Trigger alignWheelsButton;
  private Scheduler scheduler;
  private boolean isolatedTestMode;

  public static MatchData.OwnedSide getScale() {
    return SCALE;
  }

  public static MatchData.OwnedSide getNearSwitch() {
    return NEAR_SWITCH;
  }

  public static MatchData.OwnedSide getFarSwitch() {
    return FAR_SWITCH;
  }

  @Override
  public void autonomousInit() {
    NEAR_SWITCH = MatchData.getOwnedSide(MatchData.GameFeature.SWITCH_NEAR);
    FAR_SWITCH = MatchData.getOwnedSide(MatchData.GameFeature.SWITCH_FAR);
    SCALE = MatchData.getOwnedSide(MatchData.GameFeature.SCALE);
  }

  @Override
  public void robotInit() {
    settings = INJECTOR.settings();
    scheduler = Scheduler.getInstance();
    isolatedTestMode = settings.isIsolatedTestMode();
    TelemetryService telemetryService = INJECTOR.telemetryService();
    if (!isolatedTestMode) {
      driveSubsystem = INJECTOR.driveSubsystem();
      alignWheelsButton = INJECTOR.alignWheelsTrigger();
      INJECTOR.graphables().forEach(g -> g.register(telemetryService));
      driveSubsystem.zeroAzimuthEncoders();
    } else {
      logger.warn("running in SOB mode");
    }
    LiveWindow.disableAllTelemetry();
    telemetryService.start();
  }

  @Override
  public void teleopInit() {
    if (!isolatedTestMode) {
      driveSubsystem.stop();
      logger.info("teleopInit - stopped swerve");
    }
  }

  @Override
  public void teleopPeriodic() {
    scheduler.run();
  }

  @Override
  public void disabledInit() {
    logger.info("disabledInit");
  }

  @Override
  public void disabledPeriodic() {
    if (isolatedTestMode) return;
    if (alignWheelsButton.hasActivated()) {
      driveSubsystem.alignWheels();
    }
  }

  @Override
  public void robotPeriodic() {}
}
