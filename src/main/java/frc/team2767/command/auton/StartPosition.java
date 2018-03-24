package frc.team2767.command.auton;

public enum StartPosition {
  UNKNOWN(0),
  LEFT(90),
  CENTER(0),
  RIGHT(-90.0);

  private final double azOffset;

  StartPosition(double azOffset) {
    this.azOffset = azOffset;
  }

  public double getPathAngle(double angle) {
    return angle + azOffset;
  }
}
