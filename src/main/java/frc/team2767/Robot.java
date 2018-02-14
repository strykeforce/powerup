package frc.team2767;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.PrintCommand;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import frc.team2767.command.LogCommand;
import frc.team2767.command.OwnedSidesSettable;
import frc.team2767.command.auton.CenterSwitchCommand;
import frc.team2767.control.Controls;
import frc.team2767.control.SimpleTrigger;
import frc.team2767.subsystem.DriveSubsystem;
import java.io.File;
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
  private static final int AUTON_SWITCH_STABLE = 100;
  private static final Logger logger = LoggerFactory.getLogger(Robot.class);

  static {
    File robotConfig = new File("/home/lvuser/powerup.toml");
    URL thirdCoastConfig = Robot.class.getResource("/META-INF/powerup/thirdcoast.toml");

    logger.info("loading robot configuration from {}", robotConfig);
    logger.info("loading Third Coast configuration from {}", thirdCoastConfig);
    INJECTOR =
        DaggerSingletonComponent.builder()
            .robotConfig(robotConfig)
            .thirdCoastConfig(thirdCoastConfig)
            .build();
  }

  private int autonSwitchStableCount = 0;
  private int newAutonSwitchPostion = -1;
  private Controls controls;
  private DriveSubsystem driveSubsystem;
  private SimpleTrigger alignWheelsButton;
  private Scheduler scheduler;
  private boolean isolatedTestMode;
  private Command autonCommand = new PrintCommand("NO AUTON SELECTED");
  private MatchData.OwnedSide nearSwitch;
  private MatchData.OwnedSide scale;
  private int autonSwitchPosition = -1;
  private boolean doneCheckingMatchData = false;

  @Override
  public void robotInit() {
    Settings settings = INJECTOR.settings();
    controls = INJECTOR.controls();
    scheduler = Scheduler.getInstance();

    TelemetryService telemetryService = INJECTOR.telemetryService();
    isolatedTestMode = settings.isIsolatedTestMode();
    if (isolatedTestMode) {
      logger.warn("starting {}", isolatedTestModeMessage());
      return;
    }
    // TODO: skip a lot of this stuff if in competition
    driveSubsystem = INJECTOR.driveSubsystem();
    alignWheelsButton = INJECTOR.alignWheelsTrigger();
    INJECTOR.graphables().forEach(g -> g.register(telemetryService));
    telemetryService.start();
    driveSubsystem.zeroAzimuthEncoders();
    LiveWindow.disableAllTelemetry();
    // start camera display to smartdashbard
    CameraServer.getInstance().startAutomaticCapture();
  }

  @Override
  public void robotPeriodic() {}

  @Override
  public void disabledInit() {
    logger.info("DISABLED {}", isolatedTestModeMessage());
    Logging.flushLogs();
  }

  @Override
  public void disabledPeriodic() {
    checkMatchData();
    // auton commands need time to compute path trajectories so instantiate as early as possible
    if (checkAutonomousSwitch()) {
      logger.info("initializing auton command {}", String.format("%02X", autonSwitchPosition));
      // use hexadecimal notation below to correspond to switch input, range is [0x00, 0x3F]
      switch (autonSwitchPosition) {
        case 0x00:
          // active when switch missing, leave empty
          break;
        case 0x01:
          autonCommand = new CenterSwitchCommand();
          break;
        case 0x30:
          autonCommand = new LogCommand("Running auton command 0x30");
          break;
        default:
          logger.warn(
              "no auton command assigned for switch position {}",
              String.format("%02X", autonSwitchPosition));
          break;
      }
    }
    if (isolatedTestMode) return;
    if (alignWheelsButton.hasActivated()) {
      driveSubsystem.alignWheels();
    }
  }

  private boolean checkMatchData() {
    boolean changed = false;
    OwnedSide ownedSide = MatchData.getOwnedSide(GameFeature.SWITCH_NEAR);
    if (nearSwitch != ownedSide) {
      changed = true;
      nearSwitch = ownedSide;
      logger.info("NEAR SWITCH owned side changed to {}", nearSwitch);
    }
    ownedSide = MatchData.getOwnedSide(GameFeature.SCALE);
    if (scale != ownedSide) {
      changed = true;
      scale = ownedSide;
      logger.info("SCALE owned side changed to {}", scale);
    }
    return changed;
  }

  private boolean checkAutonomousSwitch() {
    boolean changed = false;
    int switchPosition = controls.getAutonomousSwitchPosition();

    if (switchPosition != newAutonSwitchPostion) {
      autonSwitchStableCount = 0;
      newAutonSwitchPostion = switchPosition;
    } else {
      autonSwitchStableCount++;
    }

    if (autonSwitchStableCount > AUTON_SWITCH_STABLE && autonSwitchPosition != switchPosition) {
      changed = true;
      autonSwitchPosition = switchPosition;
    }
    return changed;
  }

  @Override
  public void autonomousInit() {
    logger.info("AUTONOMOUS {}", isolatedTestModeMessage());
    checkMatchData();
    if (autonCommand instanceof OwnedSidesSettable) {
      ((OwnedSidesSettable) autonCommand).setOwnedSide(nearSwitch, scale);
      logger.info("set auton command near switch owned = {}, scale owned = {}", nearSwitch, scale);
    }
    autonCommand.start();
  }

  @Override
  public void autonomousPeriodic() {
    scheduler.run();
    // log match data until it doesn't change between checks
    if (!doneCheckingMatchData) {
      doneCheckingMatchData = !checkMatchData();
    }
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
    return isolatedTestMode ? "in isolated test mode" : "";
  }
}
