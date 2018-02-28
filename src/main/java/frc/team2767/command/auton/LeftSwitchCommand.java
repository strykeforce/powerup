package frc.team2767.command.auton;

import edu.wpi.first.wpilibj.command.ConditionalCommand;
import frc.team2767.command.StartPosition;
import openrio.powerup.MatchData;

// 10
public class LeftSwitchCommand extends ConditionalCommand implements OwnedSidesSettable {

  private MatchData.OwnedSide ownedSide = MatchData.OwnedSide.UNKNOWN;

  public LeftSwitchCommand() {
    super(new LeftSwitchCommandGroup("left_switch"), new LeftSwitchCommandGroup("left_switch"));
  }

  @Override
  protected boolean condition() {
    return ownedSide == MatchData.OwnedSide.LEFT;
  }

  @Override
  public void setOwnedSide(
      StartPosition startPosition, MatchData.OwnedSide nearSwitch, MatchData.OwnedSide scale) {
    this.ownedSide = scale;
  }
}
