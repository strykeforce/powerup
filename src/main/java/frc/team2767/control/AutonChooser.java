package frc.team2767.control;

import static frc.team2767.command.auton.StartPosition.*;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.command.LogCommand;
import frc.team2767.command.auton.CrossTheLineCommandGroup;
import frc.team2767.command.auton.OwnedSidesSettable;
import frc.team2767.command.auton.StartPosition;
import javax.inject.Inject;
import openrio.powerup.MatchData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutonChooser {
  static final Logger logger = LoggerFactory.getLogger(AutonChooser.class);
  private static final int AUTON_SWITCH_DEBOUNCED = 100;
  private final Controls controls;

  private Command command;
  private int autonSwitchStableCount = 0;
  private int autonSwitchPosition = -1;
  private StartPosition startPosition;
  private int newAutonSwitchPosition;
  private AutonPracticeChooser autonPracticeChooser;

  @Inject
  public AutonChooser(Controls controls) {
    this.controls = controls;
  }

  public void checkAutonSwitch() {
    // auton commands need time to compute path trajectories so instantiate as early as possible
    if (checkAutonomousSwitch()) {
      logger.info(
          "auton switch initializing auton command {}, start position = {}",
          String.format("%02X", autonSwitchPosition),
          startPosition);
      if (isAutonTest()) autonPracticeChooser = new AutonPracticeChooser(autonSwitchPosition);
      else command = AutonCommands.createFor(autonSwitchPosition, startPosition);
    }
  }

  private boolean isAutonTest() {
    return autonSwitchPosition > 0x00 && autonSwitchPosition < 0x04;
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

  public void reset() {
    if (autonSwitchPosition == -1) return;
    logger.debug("reset auton chooser");
    command = new LogCommand("NO AUTON SELECTED");
    autonSwitchPosition = -1;
    startPosition = UNKNOWN;
  }

  public StartPosition getStartPosition() {
    if (isAutonTest()) return autonPracticeChooser.getStartPosition();
    return startPosition;
  }

  public Command getCommand() {
    if (isAutonTest()) return autonPracticeChooser.getCommand();
    else updateCommandWithGameData();
    return command;
  }

  private void updateCommandWithGameData() {
    logger.info("setting autonomous command owned sides");
    MatchData.OwnedSide nearSwitch = MatchData.OwnedSide.UNKNOWN;
    MatchData.OwnedSide scale = MatchData.OwnedSide.UNKNOWN;
    long start = System.nanoTime();

    while (nearSwitch == MatchData.OwnedSide.UNKNOWN && System.nanoTime() - start < 5e9) {
      nearSwitch = MatchData.getOwnedSide(MatchData.GameFeature.SWITCH_NEAR);
      scale = MatchData.getOwnedSide(MatchData.GameFeature.SCALE);
    }

    if (nearSwitch == MatchData.OwnedSide.UNKNOWN) {
      logger.error("GAME DATA TIMEOUT");
      switch (startPosition) {
        case UNKNOWN:
          String msg = "invalid auton switch position - start position unknown";
          logger.error(msg);
          command = new LogCommand(msg);
          break;
        case LEFT:
          command = new CrossTheLineCommandGroup(CrossTheLineCommandGroup.Side.LEFT);
          break;
        case CENTER:
          command = new CrossTheLineCommandGroup(CrossTheLineCommandGroup.Side.CENTER);
          break;
        case RIGHT:
          command = new CrossTheLineCommandGroup(CrossTheLineCommandGroup.Side.RIGHT);
          break;
      }
    } else {
      logger.info("NEAR SWITCH owned side = {}", nearSwitch);
      logger.info("SCALE owned side = {}", scale);
    }

    if (command instanceof OwnedSidesSettable)
      ((OwnedSidesSettable) command).setOwnedSide(startPosition, nearSwitch, scale);
  }
}
