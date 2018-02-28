package frc.team2767.command.auton;

import edu.wpi.first.wpilibj.command.ConditionalCommand;
import frc.team2767.command.StartPosition;
import openrio.powerup.MatchData.OwnedSide;

public class RightSwitchCommand extends ConditionalCommand implements OwnedSidesSettable {

  private OwnedSide ownedSide = OwnedSide.UNKNOWN;

  public RightSwitchCommand() {
    super(new RightSwitchCommandGroup("right_scale"), new RightSwitchCommandGroup("right_scale"));
  }

  @Override
  protected boolean condition() {
    return ownedSide == OwnedSide.LEFT;
  }

  @Override
  public void setOwnedSide(StartPosition startPosition, OwnedSide nearSwitch, OwnedSide scale) {
    this.ownedSide = scale;
  }
}
