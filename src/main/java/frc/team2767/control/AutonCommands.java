package frc.team2767.control;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.command.LogCommand;
import frc.team2767.command.auton.CornerConditionalCommand;
import frc.team2767.command.auton.nearswitch.CenterSwitchCommand;
import frc.team2767.command.auton.nearswitch.OppositeSwitchCommandGroup;
import frc.team2767.command.auton.nearswitch.SwitchCommandGroup;
import frc.team2767.command.auton.scale.OppositeScaleCommandGroup;
import frc.team2767.command.auton.scale.ScaleCommandGroup;
import frc.team2767.command.auton.scale.ScaleSettings;
import frc.team2767.command.auton.scale.TwoCubeScaleRightCommandGroup;
import frc.team2767.command.test.LifeCycleTestCommand;

public class AutonCommands {

  static Command createFor(int switchPosition) {
    // use hexadecimal notation below to correspond to switch input, range is [0x00, 0x3F]
    // Most significant digit: 1 - Left, 2 - Center, 3 - Right

    Command command;
    switch (switchPosition) {
      case 0x10: // left corner, scale priority
        Command leftScale = new ScaleCommandGroup(ScaleSettings.LEFT);
        command =
            new CornerConditionalCommand(
                new SwitchCommandGroup(SwitchCommandGroup.Side.LEFT),
                leftScale,
                leftScale,
                new OppositeScaleCommandGroup(OppositeScaleCommandGroup.Side.LEFT));
        break;
      case 0x11: // left corner, switch priority
        Command leftSwitch = new SwitchCommandGroup(SwitchCommandGroup.Side.LEFT);
        command =
            new CornerConditionalCommand(
                leftSwitch,
                new ScaleCommandGroup(ScaleSettings.LEFT),
                leftSwitch,
                new OppositeSwitchCommandGroup(OppositeSwitchCommandGroup.Side.LEFT));
        break;
      case 0x12: // left corner, scale priority, opposite switch
        leftScale = new ScaleCommandGroup(ScaleSettings.LEFT);
        command =
            new CornerConditionalCommand(
                new SwitchCommandGroup(SwitchCommandGroup.Side.LEFT),
                leftScale,
                leftScale,
                new OppositeSwitchCommandGroup(OppositeSwitchCommandGroup.Side.LEFT));
        break;
      case 0x13: // left corner, always switch
        leftSwitch = new SwitchCommandGroup(SwitchCommandGroup.Side.LEFT);
        Command rightSwitch = new SwitchCommandGroup(SwitchCommandGroup.Side.RIGHT);
        command = new CornerConditionalCommand(leftSwitch, rightSwitch, leftSwitch, rightSwitch);
        break;
      case 0x14: // left corner, always scale
        leftScale = new ScaleCommandGroup(ScaleSettings.LEFT);
        Command rightScale = new ScaleCommandGroup(ScaleSettings.RIGHT);
        command = new CornerConditionalCommand(rightScale, leftScale, leftScale, rightScale);
        break;
      case 0x1F: // left corner, test
        command =
            new CornerConditionalCommand(
                new LifeCycleTestCommand("Left Near Switch"),
                new LifeCycleTestCommand("Left Scale"),
                new LifeCycleTestCommand("Left Both"),
                new LifeCycleTestCommand("Left Neither"));
        break;
      case 0x20: // center switch
        command = new CenterSwitchCommand();
        break;
      case 0x30: // right corner, scale priority
        rightScale = new ScaleCommandGroup(ScaleSettings.RIGHT);
        command =
            new CornerConditionalCommand(
                new SwitchCommandGroup(SwitchCommandGroup.Side.RIGHT),
                rightScale,
                rightScale,
                new OppositeScaleCommandGroup(OppositeScaleCommandGroup.Side.RIGHT));
        break;
      case 0x31: // right corner, switch priority
        rightSwitch = new SwitchCommandGroup(SwitchCommandGroup.Side.RIGHT);
        command =
            new CornerConditionalCommand(
                rightSwitch,
                new ScaleCommandGroup(ScaleSettings.RIGHT),
                rightSwitch,
                new OppositeSwitchCommandGroup(OppositeSwitchCommandGroup.Side.RIGHT));
        break;
      case 0x32: // right corner, scale priority, opposite switch
        rightScale = new ScaleCommandGroup(ScaleSettings.RIGHT);
        command =
            new CornerConditionalCommand(
                new SwitchCommandGroup(SwitchCommandGroup.Side.RIGHT),
                rightScale,
                rightScale,
                new OppositeSwitchCommandGroup(OppositeSwitchCommandGroup.Side.RIGHT));
        break;
      case 0x33: // right corner, always switch
        leftSwitch = new SwitchCommandGroup(SwitchCommandGroup.Side.LEFT);
        rightSwitch = new SwitchCommandGroup(SwitchCommandGroup.Side.RIGHT);
        command = new CornerConditionalCommand(rightSwitch, leftSwitch, rightSwitch, leftSwitch);
        break;
      case 0x34: // right corner, always scale
        leftScale = new ScaleCommandGroup(ScaleSettings.LEFT);
        rightScale = new TwoCubeScaleRightCommandGroup();
        command = new CornerConditionalCommand(leftScale, rightScale, rightScale, leftScale);
        break;
      case 0x3F: // right corner, test
        command =
            new CornerConditionalCommand(
                new LifeCycleTestCommand("Right Near Switch"),
                new LifeCycleTestCommand("Right Scale"),
                new LifeCycleTestCommand("Right Both"),
                new LifeCycleTestCommand("Right Neither"));
        break;
      case 0x00:
      default:
        String msg =
            String.format("no auton command assigned for switch position %02X", switchPosition);
        DriverStation.reportWarning(msg, false);
        AutonChooser.logger.warn(msg);
        command = new LogCommand("Invalid auton switch position");
        break;
    }
    return command;
  }
}
