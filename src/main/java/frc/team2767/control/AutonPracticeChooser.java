package frc.team2767.control;

import static frc.team2767.command.auton.StartPosition.*;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2767.command.LogCommand;
import frc.team2767.command.auton.OwnedSidesSettable;
import frc.team2767.command.auton.StartPosition;
import openrio.powerup.MatchData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutonPracticeChooser {

  private static final Logger logger = LoggerFactory.getLogger(AutonPracticeChooser.class);
  private final int autonSwitchPosition;
  private SendableChooser<Command> commandChooser;
  private SendableChooser<MatchData.OwnedSide> switchSideChooser;
  private SendableChooser<MatchData.OwnedSide> scaleSideChooser;
  private StartPosition startPosition;

  public AutonPracticeChooser(int autonSwitchPosition) {
    this.autonSwitchPosition = autonSwitchPosition;
    initChoosers();
    initStartPosition();
  }

  private void initChoosers() {
    switchSideChooser = new SendableChooser<>();
    switchSideChooser.addDefault("Unknown", MatchData.OwnedSide.UNKNOWN);
    switchSideChooser.addObject("Left", MatchData.OwnedSide.LEFT);
    switchSideChooser.addObject("Right", MatchData.OwnedSide.RIGHT);
    SmartDashboard.putData("Test/SwitchSideChooser", switchSideChooser);

    scaleSideChooser = new SendableChooser<>();
    scaleSideChooser.addDefault("Unknown", MatchData.OwnedSide.UNKNOWN);
    scaleSideChooser.addObject("Left", MatchData.OwnedSide.LEFT);
    scaleSideChooser.addObject("Right", MatchData.OwnedSide.RIGHT);
    SmartDashboard.putData("Test/ScaleSideChooser", scaleSideChooser);

    commandChooser = new SendableChooser<>();
    commandChooser.addDefault("Select Auton", new LogCommand("no test auton selected"));

    switch (autonSwitchPosition) {
      case 0x01:
        commandChooser.addObject(
            "10: scale always", AutonCommands.createFor(0x10, StartPosition.LEFT));
        commandChooser.addObject(
            "11: switch priority, scale if same side",
            AutonCommands.createFor(0x11, StartPosition.LEFT));
        commandChooser.addObject(
            "12: switch always, scale first if same side as switch",
            AutonCommands.createFor(0x12, StartPosition.LEFT));
        commandChooser.addObject(
            "13: scale always, switch second if same side as scale",
            AutonCommands.createFor(0x13, StartPosition.LEFT));
        commandChooser.addObject(
            "14: switch only", AutonCommands.createFor(0x14, StartPosition.LEFT));
        commandChooser.addObject("1F: log only", AutonCommands.createFor(0x1F, StartPosition.LEFT));
        break;
      case 0x02:
        commandChooser.addObject(
            "20 - center switch", AutonCommands.createFor(0x20, StartPosition.CENTER));
        break;
      case 0x03:
        commandChooser.addObject(
            "30: scale always", AutonCommands.createFor(0x30, StartPosition.LEFT));
        commandChooser.addObject(
            "31: switch priority, scale if same side",
            AutonCommands.createFor(0x31, StartPosition.LEFT));
        commandChooser.addObject(
            "32: witch always, scale first if same side as switch",
            AutonCommands.createFor(0x32, StartPosition.LEFT));
        commandChooser.addObject(
            "33: scale always, switch second if same side as scale",
            AutonCommands.createFor(0x33, StartPosition.LEFT));
        commandChooser.addObject(
            "34: switch only", AutonCommands.createFor(0x34, StartPosition.LEFT));
        commandChooser.addObject("3F: log only", AutonCommands.createFor(0x3F, StartPosition.LEFT));
        break;
      default:
        logger.error("invalid auton switch position {}", autonSwitchPosition);
    }

    SmartDashboard.putData("Test/CommandChooser", commandChooser);
  }

  private void initStartPosition() {
    switch (autonSwitchPosition) {
      case 0x01:
        startPosition = LEFT;
        break;
      case 0x02:
        startPosition = CENTER;
        break;
      case 0x03:
        startPosition = RIGHT;
        break;
      default:
        logger.error("invalid switch position {}", autonSwitchPosition);
    }
  }

  public Command getCommand() {
    Command command = commandChooser.getSelected();
    MatchData.OwnedSide nearSwitch = switchSideChooser.getSelected();
    MatchData.OwnedSide scale = scaleSideChooser.getSelected();
    if (command instanceof OwnedSidesSettable)
      ((OwnedSidesSettable) command).setOwnedSide(startPosition, nearSwitch, scale);
    return command;
  }

  public StartPosition getStartPosition() {
    return startPosition;
  }
}
