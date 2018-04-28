package frc.team2767.control;

import static frc.team2767.command.auton.PowerUpGameFeature.*;
import static openrio.powerup.MatchData.OwnedSide.LEFT;
import static openrio.powerup.MatchData.OwnedSide.RIGHT;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.command.auton.CenterSwitchCommand;
import frc.team2767.command.auton.PowerUpAutonCommand;
import frc.team2767.command.auton.StartPosition;

public class AutonCommands {

  static Command createFor(int switchPosition, StartPosition startPosition) {
    // use hexadecimal notation below to correspond to switch input, range is [0x00, 0x3F]
    // Most significant digit: 1 - Left, 2 - Center, 3 - Right

    if (switchPosition == 0x20) return new CenterSwitchCommand();

    PowerUpAutonCommand command = new PowerUpAutonCommand(startPosition);
    switch (switchPosition) {
        // scale always
      case 0x10:
        command.addScenario(LEFT, RIGHT, SCALE, SCALE, NONE);
        command.addScenario(RIGHT, LEFT, SCALE, SCALE, SCALE);
        command.addScenario(LEFT, LEFT, SCALE, SCALE, SCALE);
        command.addScenario(RIGHT, RIGHT, SCALE, SCALE, NONE);
        break;
      case 0x30:
        command.addScenario(RIGHT, LEFT, SCALE, SCALE, NONE);
        command.addScenario(LEFT, RIGHT, SCALE, SCALE, SCALE);
        command.addScenario(RIGHT, RIGHT, SCALE, SCALE, SCALE);
        command.addScenario(LEFT, LEFT, SCALE, SCALE, NONE);
        break;

        // switch priority, scale if same side
      case 0x11:
        command.addScenario(LEFT, RIGHT, SWITCH, SWITCH, SWITCH);
        command.addScenario(RIGHT, LEFT, SCALE, SCALE, SCALE);
        command.addScenario(LEFT, LEFT, SCALE, SWITCH, NONE);
        command.addScenario(RIGHT, RIGHT, SWITCH, NONE, NONE);
        break;
      case 0x31:
        command.addScenario(RIGHT, LEFT, SWITCH, SWITCH, SWITCH);
        command.addScenario(LEFT, RIGHT, SCALE, SCALE, SCALE);
        command.addScenario(RIGHT, RIGHT, SCALE, SWITCH, NONE);
        command.addScenario(LEFT, LEFT, SWITCH, NONE, NONE);
        break;

        // switch always, scale first if same side as switch
      case 0x12:
        command.addScenario(LEFT, RIGHT, SWITCH, SWITCH, SWITCH);
        command.addScenario(RIGHT, LEFT, SWITCH, NONE, NONE);
        command.addScenario(LEFT, LEFT, SCALE, SWITCH, NONE);
        command.addScenario(RIGHT, RIGHT, SCALE, SWITCH, NONE);
        break;
      case 0x32:
        command.addScenario(RIGHT, LEFT, SWITCH, SWITCH, SWITCH);
        command.addScenario(LEFT, RIGHT, SWITCH, NONE, NONE);
        command.addScenario(RIGHT, RIGHT, SCALE, SWITCH, NONE);
        command.addScenario(LEFT, LEFT, SCALE, SWITCH, NONE);
        break;

        // scale always, switch second if same side as scale
      case 0x13:
        command.addScenario(LEFT, RIGHT, SCALE, SCALE, NONE);
        command.addScenario(RIGHT, LEFT, SCALE, SCALE, SCALE);
        command.addScenario(LEFT, LEFT, SCALE, SWITCH, NONE);
        command.addScenario(RIGHT, RIGHT, SCALE, SWITCH, NONE);
        break;
      case 0x33:
        command.addScenario(RIGHT, LEFT, SCALE, SCALE, NONE);
        command.addScenario(LEFT, RIGHT, SCALE, SCALE, SCALE);
        command.addScenario(RIGHT, RIGHT, SCALE, SWITCH, NONE);
        command.addScenario(LEFT, LEFT, SCALE, SWITCH, NONE);
        break;

        // switch only
      case 0x14:
        command.addScenario(LEFT, RIGHT, SWITCH, NONE, NONE);
        command.addScenario(RIGHT, LEFT, SWITCH, NONE, NONE);
        command.addScenario(LEFT, LEFT, SWITCH, NONE, NONE);
        command.addScenario(RIGHT, RIGHT, SWITCH, NONE, NONE);
        break;
      case 0x34:
        command.addScenario(RIGHT, LEFT, SWITCH, NONE, NONE);
        command.addScenario(LEFT, RIGHT, SWITCH, NONE, NONE);
        command.addScenario(RIGHT, RIGHT, SWITCH, NONE, NONE);
        command.addScenario(LEFT, LEFT, SWITCH, NONE, NONE);
        break;

        // Elim Auton
      case 0x35:
        command.addScenario(RIGHT, LEFT, SWITCH, SWITCH, SWITCH);
        command.addScenario(LEFT, RIGHT, SCALE, SCALE, SCALE);
        command.addScenario(RIGHT, RIGHT, SCALE, SCALE, SCALE);
        command.addScenario(LEFT, LEFT, COOPERATIVE, NONE, NONE);
        break;
      case 0x15:
        command.addScenario(LEFT, RIGHT, SWITCH, SWITCH, SWITCH);
        command.addScenario(RIGHT, LEFT, SCALE, SCALE, SCALE);
        command.addScenario(LEFT, LEFT, SCALE, SCALE, SCALE);
        command.addScenario(RIGHT, RIGHT, COOPERATIVE, NONE, NONE);
        break;

      case 0x16:
        command.addScenario(LEFT, RIGHT, COOPERATIVE, NONE, NONE);
        command.addScenario(RIGHT, LEFT, SCALE, SCALE, SCALE);
        command.addScenario(LEFT, LEFT, SCALE, SCALE, SCALE);
        command.addScenario(RIGHT, RIGHT, COOPERATIVE, NONE, NONE);
        break;
      case 0x36:
        command.addScenario(RIGHT, LEFT, COOPERATIVE, NONE, NONE);
        command.addScenario(LEFT, RIGHT, SCALE, SCALE, SCALE);
        command.addScenario(RIGHT, RIGHT, SCALE, SCALE, SCALE);
        command.addScenario(LEFT, LEFT, COOPERATIVE, NONE, NONE);
        break;

        // test
      case 0x1F:
        command.addScenario(LEFT, RIGHT, NONE, NONE, NONE);
        command.addScenario(RIGHT, LEFT, NONE, NONE, NONE);
        command.addScenario(LEFT, LEFT, NONE, NONE, NONE);
        command.addScenario(RIGHT, RIGHT, NONE, NONE, NONE);
        break;
      case 0x3F:
        command.addScenario(RIGHT, LEFT, NONE, NONE, NONE);
        command.addScenario(LEFT, RIGHT, NONE, NONE, NONE);
        command.addScenario(RIGHT, RIGHT, NONE, NONE, NONE);
        command.addScenario(LEFT, LEFT, NONE, NONE, NONE);
        break;
      case 0x00:
      default:
        String msg =
            String.format("no auton command assigned for switch position %02X", switchPosition);
        DriverStation.reportWarning(msg, false);
        AutonChooser.logger.warn(msg);
        break;
    }
    return command;
  }
}
