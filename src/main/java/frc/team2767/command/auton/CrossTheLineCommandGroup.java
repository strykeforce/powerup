package frc.team2767.command.auton;

/**
 * Fail-safe command, depends on starting position.
 *
 * <p>Center = onTrue command, Left or Right = onFalse command
 */
public class CrossTheLineCommandGroup extends PowerUpCommandGroup {
  private static final double START_POSITION_YAW = 0d;

  public CrossTheLineCommandGroup(Side side) {
    addSequential(new PathCommand(side.path, side.startPosition.getPathAngle(START_POSITION_YAW)));
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
