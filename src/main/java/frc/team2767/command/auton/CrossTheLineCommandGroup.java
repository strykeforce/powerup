package frc.team2767.command.auton;

import frc.team2767.command.StartPosition;

/**
 * Fail-safe command, depends on starting position.
 *
 * <p>Center = onTrue command, Left or Right = onFalse command
 */
public class CrossTheLineCommandGroup extends PowerUpCommandGroup {

  public CrossTheLineCommandGroup(Side side) {
    super();
    addSequential(new PathCommand(side.path, side.startPosition));
  }

  public enum Side {
    LEFT("straight_line", StartPosition.LEFT),
    RIGHT("straight_line", StartPosition.RIGHT),
    CENTER("center_left", StartPosition.CENTER);

    private final String path;
    private final StartPosition startPosition;

    Side(String path, StartPosition startPosition) {
      this.path = path;
      this.startPosition = startPosition;
    }
  }
}
