package frc.team2767.command.auton.nearswitch;

import edu.wpi.first.wpilibj.command.ConditionalCommand;
import frc.team2767.command.StartPosition;
import frc.team2767.command.auton.OwnedSidesSettable;
import openrio.powerup.MatchData.OwnedSide;

/**
 * Command group for autonomous mode.
 *
 * <p>Owned side LEFT = onTrue command, Owned side RIGHT = onFalse command
 */
public class CenterSwitchCommand extends ConditionalCommand implements OwnedSidesSettable {

  private OwnedSide ownedSide = OwnedSide.UNKNOWN;

  public CenterSwitchCommand() {
    super(
        new CenterSwitchCommandGroup("center_left"), new CenterSwitchCommandGroup("center_right"));
  }

  @Override
  protected boolean condition() {
    return ownedSide == OwnedSide.LEFT;
  }

  @Override
  public void setOwnedSide(StartPosition startPosition, OwnedSide nearSwitch, OwnedSide scale) {
    this.ownedSide = nearSwitch;
    // don't care about scale
  }
}
