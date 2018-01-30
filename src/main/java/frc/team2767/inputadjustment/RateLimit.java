package frc.team2767.inputadjustment;

/** Applies rate limit to joystick input. */
public class RateLimit {

  private final double rateLimit;
  private double lastLimit;

  public RateLimit(double rateLimit, double lastLimit) {
    this.lastLimit = lastLimit;
    this.rateLimit = rateLimit;
  }

  public double applyRateLimit(double joystickInput) {
    double y;
    if (Math.abs(joystickInput - lastLimit) > rateLimit) {
      y = lastLimit + Math.copySign(rateLimit, joystickInput - lastLimit);
    } else {
      y = joystickInput;
    }

    lastLimit = y;
    return y;
  }
}
