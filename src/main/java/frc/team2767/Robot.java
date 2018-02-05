package frc.team2767;

import com.moandjiezana.toml.Toml;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import frc.team2767.control.Trigger;
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

  private boolean sob;
  private Trigger alignWheelsButton;

  @Override
  public void autonomousInit() {
    NEAR_SWITCH = MatchData.getOwnedSide(MatchData.GameFeature.SWITCH_NEAR);
    FAR_SWITCH = MatchData.getOwnedSide(MatchData.GameFeature.SWITCH_FAR);
    SCALE = MatchData.getOwnedSide(MatchData.GameFeature.SCALE);
  }

  @Override
  public void autonomousInit() {
    NEAR_SWITCH = MatchData.getOwnedSide(MatchData.GameFeature.SWITCH_NEAR);
    FAR_SWITCH = MatchData.getOwnedSide(MatchData.GameFeature.SWITCH_FAR);
    SCALE = MatchData.getOwnedSide(MatchData.GameFeature.SCALE);
  }

  @Override
  public void robotInit() {
    Toml settings = INJECTOR.settings().getTable(TABLE);
    sob = settings.getBoolean("sob", false);
    logger.info(settings.getString("description"));
    TelemetryService telemetryService = INJECTOR.telemetryService();
    if (!sob) {
      alignWheelsButton = INJECTOR.alignWheelsTrigger();
      INJECTOR.graphables().forEach(g -> g.register(telemetryService));
      INJECTOR.driveSubsystem().zeroAzimuthEncoders();
    } else {
      logger.warn("running in SOB mode");
      //      INJECTOR.intakeSubsystem().register(telemetryService);
      INJECTOR.climberSubsystem().register(telemetryService);
      INJECTOR.controls();
    }
    LiveWindow.disableAllTelemetry();
    telemetryService.start();
  }

  @Override
  public void teleopInit() {
    logger.info("teleopInit - stopping swerve");
    if (!sob) INJECTOR.driveSubsystem().stop();
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
    if (sob) return;
    if (alignWheelsButton.hasActivated()) {
      INJECTOR.driveSubsystem().alignWheels();
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
