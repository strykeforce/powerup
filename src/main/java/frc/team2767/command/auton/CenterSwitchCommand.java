package frc.team2767.command.auton;

import edu.wpi.first.wpilibj.command.ConditionalCommand;
import frc.team2767.command.OwnedSidesSettable;
import frc.team2767.command.test.PathCommand;
import openrio.powerup.MatchData.OwnedSide;

/**
 * Command group for autonomous mode.
 *
 * <p>Owned side LEFT = onTrue command, Owned side RIGHT = onFalse command
 */
public class CenterSwitchCommand extends ConditionalCommand implements OwnedSidesSettable {

  private static final int LEFT = 1;
  private static final int CENTER_LEFT = 2;
  private static final int CENTER_RIGHT = 3;
  private static final int CENTER_RIGHT_EXCHANGE = 4;

  private OwnedSide ownedSide = OwnedSide.UNKNOWN;

  public CenterSwitchCommand() {
    super(new PathCommand(CENTER_LEFT), new PathCommand(CENTER_RIGHT));
  }

  @Override
  protected boolean condition() {
    return ownedSide == OwnedSide.LEFT;
  }

  @Override
  public void setOwnedSide(OwnedSide nearSwitch, OwnedSide scale) {
    this.ownedSide = nearSwitch;
    // don't care about scale
  }
}
