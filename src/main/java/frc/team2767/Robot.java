package frc.team2767;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.PrintCommand;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import frc.team2767.command.OwnedSidesSettable;
import frc.team2767.command.auton.CenterSwitchCommand;
import frc.team2767.control.Controls;
import frc.team2767.control.Trigger;
import frc.team2767.subsystem.DriveSubsystem;
import java.io.File;
import openrio.powerup.MatchData;
import openrio.powerup.MatchData.GameFeature;
import openrio.powerup.MatchData.OwnedSide;
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

  private Controls controls;
  private DriveSubsystem driveSubsystem;
  private Trigger alignWheelsButton;
  private Scheduler scheduler;
  private boolean isolatedTestMode;
  private Command autonCommand = new PrintCommand("NO AUTON SELECTED");
  private MatchData.OwnedSide nearSwitch = OwnedSide.UNKNOWN;
  private MatchData.OwnedSide scale = OwnedSide.UNKNOWN;
  private int autonSwitchPosition = -1;
  private long autonSwitchLastChangedTime;
  private boolean doneCheckingMatchData = false;
  private long kAutonSwitchDebounceMs;

  @Override
  public void robotInit() {
    Settings settings = INJECTOR.settings();
    kAutonSwitchDebounceMs = settings.getTable(TABLE).getLong("autonSwitchDebounceMs", 1000L);
    controls = INJECTOR.controls();
    scheduler = Scheduler.getInstance();
    // TODO: skip a lot of this stuff if in compitition
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
    logger.debug("autonSwitchDebounceMs = {}", kAutonSwitchDebounceMs);
  }

  @Override
  public void robotPeriodic() {}

  @Override
  public void disabledInit() {
    logger.warn("disabled mode starting");
  }

  @Override
  public void disabledPeriodic() {
    if (isolatedTestMode) return;
    if (alignWheelsButton.hasActivated()) {
      driveSubsystem.alignWheels();
    }
    checkMatchData();
    // auton commands need time to compute path trajectories so instantiate as early as possible
    if (checkAutonomousSwitch()) {
      switch (autonSwitchPosition) {
        case 0:
          autonCommand = new CenterSwitchCommand();
          break;
        default:
          break;
      }
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
    if (switchPosition != autonSwitchPosition) {
      long now = System.currentTimeMillis();
      if (now - autonSwitchLastChangedTime < kAutonSwitchDebounceMs) return false;

      // switch hasn't changed in kAutonSwitchDebounceMs ms, so consider it set
      changed = true;
      autonSwitchPosition = switchPosition;
      autonSwitchLastChangedTime = now;
      logger.info("autonomous switch position changed to {}", autonSwitchPosition);
    }
    return changed;
  }

  @Override
  public void autonomousInit() {
    logger.warn("autonomous mode starting");
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
    logger.warn("teleop mode starting");
    if (!isolatedTestMode) {
      driveSubsystem.stop();
    }
  }

  @Override
  public void teleopPeriodic() {
    scheduler.run();
  }
}
