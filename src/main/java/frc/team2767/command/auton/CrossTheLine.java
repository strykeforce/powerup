package frc.team2767.command.auton;

import edu.wpi.first.wpilibj.command.ConditionalCommand;
import frc.team2767.command.StartPosition;
import openrio.powerup.MatchData;

/**
 * Fail-safe command, depends on starting position.
 *
 * <p>Center = onTrue command, Left or Right = onFalse command
 */
public class CrossTheLine extends ConditionalCommand implements OwnedSidesSettable {

  private StartPosition startPosition;

  public CrossTheLine() {
    super(new CenterCrossTheLine(), new DriveForward());
  }

  @Override
  protected boolean condition() {
    return startPosition == StartPosition.CENTER;
  }

  @Override
  public void setOwnedSide(
      StartPosition startPosition, MatchData.OwnedSide nearSwitch, MatchData.OwnedSide scale) {
    this.startPosition = startPosition;
  }

  public enum Side {
    LEFT,
    RIGHT,
  }

  static class CenterCrossTheLine extends PowerUpCommandGroup {
    public CenterCrossTheLine() {
      super();
      addSequential(new PathCommand("center_left"));
    }
  }

  static class DriveForward extends PowerUpCommandGroup {

    public DriveForward() {
      super();
    }
  }
}
