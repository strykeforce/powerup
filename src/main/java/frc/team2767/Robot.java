package frc.team2767;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import frc.team2767.command.LogCommand;
import frc.team2767.command.StartPosition;
import frc.team2767.command.auton.*;
import frc.team2767.command.test.LifeCycleTestCommand;
import frc.team2767.control.Controls;
import frc.team2767.control.SimpleTrigger;
import frc.team2767.subsystem.DriveSubsystem;
import openrio.powerup.MatchData;
import openrio.powerup.MatchData.GameFeature;
import openrio.powerup.MatchData.OwnedSide;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;

import java.net.URL;

import static frc.team2767.command.StartPosition.*;

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
  private Settings settings;
  private DriveSubsystem driveSubsystem;
  private SimpleTrigger alignWheelsButtons;
  private Scheduler scheduler;
  private Command autonCommand;
  private boolean autonHasRun;

  @Override
  public void robotInit() {
    settings = INJECTOR.settings();
    controls = INJECTOR.controls();
    scheduler = Scheduler.getInstance();

    logger.info("INIT in {} mode", settings.isEvent() ? "EVENT" : "SAFE");

    alignWheelsButtons = controls.getDriverControls().getAlignWheelsButtons();
    driveSubsystem = INJECTOR.driveSubsystem();
    driveSubsystem.zeroAzimuthEncoders();

    if (settings.isCameraEnabled()) CameraServer.getInstance().startAutomaticCapture();

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
    logger.info("DISABLED");
    resetAutonomous();
    Logging.flushLogs();
  }

  @Override
  public void disabledPeriodic() {
    if (alignWheelsButtons != null && alignWheelsButtons.hasActivated()) {
      logger.debug("align wheels buttons have activated");
      driveSubsystem.alignWheelsToBar();
    }

    if (autonHasRun) return;

    // auton commands need time to compute path trajectories so instantiate as early as possible
    if (checkAutonomousSwitch()) {
      logger.info(
          "auton switch initializing auton command {}, start position = {}",
          String.format("%02X", autonSwitchPosition),
          startPosition);
      // use hexadecimal notation below to correspond to switch input, range is [0x00, 0x3F]
      // Most significant digit: 1 - Left, 2 - Center, 3 - Right
      switch (autonSwitchPosition) {
        case 0x10: // left corner, scale priority
          Command leftScale = new ScaleCommandGroup(ScaleCommandGroup.Side.LEFT);
          autonCommand =
              new CornerConditionalCommand(
                  new SwitchCommandGroup(SwitchCommandGroup.Side.LEFT),
                  leftScale,
                  leftScale,
                  new NeitherCommandGroup(NeitherCommandGroup.Side.LEFT));
          break;
        case 0x11: // left corner, switch priority
          Command leftSwitch = new SwitchCommandGroup(SwitchCommandGroup.Side.LEFT);
          autonCommand =
              new CornerConditionalCommand(
                  leftSwitch,
                  new ScaleCommandGroup(ScaleCommandGroup.Side.LEFT),
                  leftSwitch,
                  new NeitherCommandGroup(NeitherCommandGroup.Side.LEFT));
          break;
        case 0x19: // left corner, test
          autonCommand =
              new CornerConditionalCommand(
                  new LifeCycleTestCommand("Left Near Switch"),
                  new LifeCycleTestCommand("Left Scale"),
                  new LifeCycleTestCommand("Left Both"),
                  new LifeCycleTestCommand("Left Neither"));
          break;
        case 0x20: // center switch
          autonCommand = new CenterSwitchCommand();
          break;
        case 0x30: // right corner, scale priority
          Command rightScale = new ScaleCommandGroup(ScaleCommandGroup.Side.RIGHT);
          autonCommand =
              new CornerConditionalCommand(
                  new SwitchCommandGroup(SwitchCommandGroup.Side.RIGHT),
                  rightScale,
                  rightScale,
                  new NeitherCommandGroup(NeitherCommandGroup.Side.RIGHT));
          break;
        case 0x31: // right corner, switch priority
          Command rightSwitch = new SwitchCommandGroup(SwitchCommandGroup.Side.RIGHT);
          autonCommand =
              new CornerConditionalCommand(
                  rightSwitch,
                  new ScaleCommandGroup(ScaleCommandGroup.Side.RIGHT),
                  rightSwitch,
                  new NeitherCommandGroup(NeitherCommandGroup.Side.RIGHT));
          break;
        case 0x39: // right corner, test
          autonCommand =
              new CornerConditionalCommand(
                  new LifeCycleTestCommand("Right Near Switch"),
                  new LifeCycleTestCommand("Right Scale"),
                  new LifeCycleTestCommand("Right Both"),
                  new LifeCycleTestCommand("Right Neither"));
          break;
        case 0x00:
        default:
          logger.warn(
              "no auton command assigned for switch position {}",
              String.format("%02X", autonSwitchPosition));
          autonCommand = new LogCommand("Invalid auton switch position");
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
    logger.info("AUTONOMOUS");

    MatchData.OwnedSide nearSwitch = OwnedSide.UNKNOWN;
    MatchData.OwnedSide scale = OwnedSide.UNKNOWN;
    long start = System.nanoTime();

    while (nearSwitch == OwnedSide.UNKNOWN && System.nanoTime() - start < 5e9) {
      nearSwitch = MatchData.getOwnedSide(GameFeature.SWITCH_NEAR);
      scale = MatchData.getOwnedSide(GameFeature.SCALE);
    }

    if (nearSwitch == OwnedSide.UNKNOWN) {
      logger.error("GAME DATA TIMEOUT");
      switch (startPosition) {
        case UNKNOWN:
          autonCommand = new LogCommand("Invalid auton switch position");
          break;
        case LEFT:
          autonCommand = new CrossTheLineCommandGroup(CrossTheLineCommandGroup.Side.LEFT);
          break;
        case CENTER:
          autonCommand = new CrossTheLineCommandGroup(CrossTheLineCommandGroup.Side.CENTER);
          break;
        case RIGHT:
          autonCommand = new CrossTheLineCommandGroup(CrossTheLineCommandGroup.Side.RIGHT);
          break;
      }
    } else {
      logger.info("NEAR SWITCH owned side = {}", nearSwitch);
      logger.info("SCALE owned side = {}", scale);
    }

    if (autonCommand instanceof OwnedSidesSettable)
      ((OwnedSidesSettable) autonCommand).setOwnedSide(startPosition, nearSwitch, scale);

    AHRS gyro = driveSubsystem.getGyro();
    gyro.setAngleAdjustment(0);
    double adj = -gyro.getAngle() % 360;
    switch (startPosition) {
      case UNKNOWN:
      case CENTER:
        break;
      case LEFT:
        adj += 90d;
        break;
      case RIGHT:
        adj -= 90d;
        break;
    }
    gyro.setAngleAdjustment(adj);
    gyro.zeroYaw();

    autonHasRun = settings.isEvent();
    autonCommand.start();
  }

  @Override
  public void autonomousPeriodic() {
    scheduler.run();
  }

  @Override
  public void teleopInit() {
    logger.info("TELEOP");
    driveSubsystem.stop();
    scheduler.removeAll();
  }

  @Override
  public void teleopPeriodic() {
    scheduler.run();
  }
}
