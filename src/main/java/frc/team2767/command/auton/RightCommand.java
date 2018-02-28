package frc.team2767.command.auton;

import edu.wpi.first.wpilibj.command.ConditionalCommand;
import frc.team2767.command.OwnedSidesSettable;
import frc.team2767.command.StartPosition;
import openrio.powerup.MatchData;

public class RightCommand extends ConditionalCommand implements OwnedSidesSettable {

  private MatchData.OwnedSide ownedSide = MatchData.OwnedSide.UNKNOWN;

  public RightCommand() {
    super(new RightScaleCommandGroup("right_scale"), new RightScaleCommandGroup("right_scale"));
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
