package frc.team2767.inputadjustment;

/**
 * Applies rate limit to joystick input.
 */
public class RateLimit {

  private static double rateLimit;
  private static double lastLimit = 0;

  public RateLimit(double rateLimit) {
    RateLimit.rateLimit = rateLimit;
  }

  public double applyRateLimit(double input) {
    double y;
    if (Math.abs(input - lastLimit) > rateLimit) {
      y = lastLimit + Math.copySign(rateLimit, input - rateLimit);
    }
    else {
      y = input;
    }

    lastLimit = y;
    return y;
  }
}
