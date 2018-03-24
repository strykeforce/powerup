package frc.team2767.command.auton;

import edu.wpi.first.wpilibj.command.Command;
import openrio.powerup.MatchData.OwnedSide;

public class CornerConditionalCommand extends Command implements OwnedSidesSettable {

  private final Command command;

  public CornerConditionalCommand(
      Command nearSwitchCommand,
      Command scaleCommand,
      Command bothCommand,
      Command neitherCommand) {
    this.command =
        new OwnedSidesSettableCommand(
            "Both/Switch,Scale,Neither",
            bothCommand,
            new OwnedSidesSettableCommand(
                "Switch/Scale,Neither",
                nearSwitchCommand,
                new OwnedSidesSettableCommand("Scale/Neither", scaleCommand, neitherCommand) {
                  @Override
                  protected boolean condition() {
                    return isSideOwned(scale);
                  }
                }) {
              @Override
              protected boolean condition() {
                return isSideOwned(nearSwitch);
              }
            }) {
          @Override
          protected boolean condition() {
            return isSideOwned(nearSwitch) && isSideOwned(scale);
          }
        };
  }

  @Override
  public void setOwnedSide(StartPosition startPosition, OwnedSide nearSwitch, OwnedSide scale) {
    assert (command instanceof OwnedSidesSettable);
    ((OwnedSidesSettable) command).setOwnedSide(startPosition, nearSwitch, scale);
  }

  @Override
  protected void initialize() {
    command.start();
  }

  @Override
  protected boolean isFinished() {
    return command.isCompleted();
  }
}
