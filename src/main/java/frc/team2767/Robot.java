package frc.team2767;

import static frc.team2767.command.StartPosition.*;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import frc.team2767.command.LogCommand;
import frc.team2767.command.OwnedSidesSettable;
import frc.team2767.command.StartPosition;
import frc.team2767.command.auton.CenterSwitchCommand;
import frc.team2767.command.auton.CrossTheLine;
import frc.team2767.command.auton.LeftCommand;
import frc.team2767.command.auton.RightCommand;
import frc.team2767.control.Controls;
import frc.team2767.control.SimpleTrigger;
import frc.team2767.subsystem.DriveSubsystem;
import frc.team2767.subsystem.Positionable;
import java.net.URL;
import openrio.powerup.MatchData;
import openrio.powerup.MatchData.GameFeature;
import openrio.powerup.MatchData.OwnedSide;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;

public class Robot extends TimedRobot {

  public static final SingletonComponent INJECTOR;
  public static final String TABLE = "POWERUP";
  private static final int AUTON_SWITCH_DEBOUNCED = 100;
  private static final Logger logger;

  static {
    URL thirdCoastConfig = Robot.class.getResource("/META-INF/powerup/thirdcoast.toml");
    INJECTOR = DaggerSingletonComponent.builder().thirdCoastConfig(thirdCoastConfig).build();
    logger = LoggerFactory.getLogger(Robot.class);
  }

  private int autonSwitchStableCount = 0;
  private int autonSwitchPosition = -1;
  private StartPosition startPosition;
  private int newAutonSwitchPosition;
  private Controls controls;
  private DriveSubsystem driveSubsystem;
  private SimpleTrigger alignWheelsButtons;
  private Scheduler scheduler;
  private boolean isolatedTestMode;
  private Command autonCommand;
  private boolean autonHasRun;

  @Override
  public void robotInit() {
    Settings settings = INJECTOR.settings();
    controls = INJECTOR.controls();
    scheduler = Scheduler.getInstance();

    logger.info("INIT in {} mode", settings.isEvent() ? "EVENT" : "SAFE");

    isolatedTestMode = settings.isIsolatedTestMode();
    if (isolatedTestMode) {
      logger.warn("INIT {}", isolatedTestModeMessage());
      return;
    }

    driveSubsystem = INJECTOR.driveSubsystem();

    alignWheelsButtons = controls.getDriverControls().getAlignWheelsButtons();

    driveSubsystem.zeroAzimuthEncoders();
    CameraServer.getInstance().startAutomaticCapture();

    LiveWindow.disableAllTelemetry();
    if (!settings.isEvent()) {
      logger.info("telemetry service enabled");
      TelemetryService telemetryService = INJECTOR.telemetryService();
      INJECTOR.graphables().forEach(g -> g.register(telemetryService));
      telemetryService.start();
    }
  }

  @Override
  public void robotPeriodic() {}

  @Override
  public void disabledInit() {
    logger.info("DISABLED {}", isolatedTestModeMessage());
    INJECTOR.positionables().forEach(Positionable::resetPosition);
    resetAutonomous();
    Logging.flushLogs();
  }

  @Override
  public void disabledPeriodic() {
    if (isolatedTestMode) return;
    if (alignWheelsButtons != null && alignWheelsButtons.hasActivated()) {
      logger.debug("align wheels buttons have activated");
      driveSubsystem.alignWheelsToBar();
    }

    if (autonHasRun) return;
    // Most significant digit: 1 - Left, 2 - Center, 3 - Right
    // auton commands need time to compute path trajectories so instantiate as early as possible
    if (checkAutonomousSwitch()) {
      logger.info(
          "initializing auton command {}, start position = {}",
          String.format("%02X", autonSwitchPosition),
          startPosition);
      // use hexadecimal notation below to correspond to switch input, range is [0x00, 0x3F]
      switch (autonSwitchPosition) {
        case 0x20:
          autonCommand = new CenterSwitchCommand();
          break;
        case 0x10:
          autonCommand = new LeftCommand();
          break;
        case 0x30:
          autonCommand = new RightCommand();
          break;
        case 0x00:
        default:
          logger.warn(
              "no auton command assigned for switch position {}",
              String.format("%02X", autonSwitchPosition));
          autonCommand = new CrossTheLine();
          break;
      }
    }
  }

  private void resetAutonomous() {
    logger.debug("reset auton");
    autonCommand = new LogCommand("NO AUTON SELECTED");
    autonSwitchPosition = -1;
    startPosition = UNKNOWN;
  }

  private boolean checkAutonomousSwitch() {
    boolean changed = false;
    int switchPosition = controls.getAutonomousSwitchPosition();

    if (switchPosition != newAutonSwitchPosition) {
      autonSwitchStableCount = 0;
      newAutonSwitchPosition = switchPosition;
    } else {
      autonSwitchStableCount++;
    }

    if (autonSwitchStableCount > AUTON_SWITCH_DEBOUNCED && autonSwitchPosition != switchPosition) {
      changed = true;
      autonSwitchPosition = switchPosition;
      switch (autonSwitchPosition >>> 4) {
        case 1:
          startPosition = LEFT;
          break;
        case 2:
          startPosition = CENTER;
          break;
        case 3:
          startPosition = RIGHT;
          break;
        default:
          startPosition = UNKNOWN;
      }
    }
    return changed;
  }

  @Override
  public void autonomousInit() {
    logger.info("AUTONOMOUS {}", isolatedTestModeMessage());

    MatchData.OwnedSide nearSwitch = OwnedSide.UNKNOWN;
    MatchData.OwnedSide scale = OwnedSide.UNKNOWN;
    long start = System.nanoTime();

    while (nearSwitch == OwnedSide.UNKNOWN && System.nanoTime() - start < 5e9) {
      nearSwitch = MatchData.getOwnedSide(GameFeature.SWITCH_NEAR);
      scale = MatchData.getOwnedSide(GameFeature.SCALE);
    }

    if (nearSwitch == OwnedSide.UNKNOWN) {
      logger.error("GAME DATA TIMEOUT");
      autonCommand = new CrossTheLine();
    } else {
      logger.info("NEAR SWITCH owned side = {}", nearSwitch);
      logger.info("SCALE owned side = {}", scale);
    }

    if (autonCommand instanceof OwnedSidesSettable)
      ((OwnedSidesSettable) autonCommand).setOwnedSide(startPosition, nearSwitch, scale);

    autonHasRun = true;
    autonCommand.start();
  }

  @Override
  public void autonomousPeriodic() {
    scheduler.run();
  }

  @Override
  public void teleopInit() {
    logger.info("TELEOP {}", isolatedTestModeMessage());
    if (!isolatedTestMode) {
      driveSubsystem.stop();
    }
  }

  @Override
  public void teleopPeriodic() {
    scheduler.run();
  }

  private String isolatedTestModeMessage() {
    return isolatedTestMode ? "(isolated test mode)" : "";
  }
}
