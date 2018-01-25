package frc.team2767.inputadjustment;

/**
 * Applies exponential scaling and deadband to joystick inputs -1.0 to 1.0.
 */
public class ExpoScale {

  private static double deadband;
  private static double scale; ;

  public ExpoScale(double deadband, double scale) {
    ExpoScale.deadband = deadband;
    ExpoScale.scale = scale;
  }

  private static final double offset = 1.0 / (scale * Math.pow(1 - deadband, 3) + (1 - scale) * (1 - deadband));

  public static double applyExpoScale(double input) {
    double y;

    y = input > 0 ? input + deadband : input - deadband;
    return (scale * Math.pow(y, 3) + (1 - scale) * y) * offset;
  }
}
