package frc.team2767.command.health;

import frc.team2767.subsystem.health.TestCase;
import java.util.Arrays;

class SwerveAzimuthTest extends VelocityTestCommand {

  public SwerveAzimuthTest() {
    super("Swerve Azimuth Motors", Arrays.asList(0, 1, 2, 3));
    TestCase testCase = newTestCase();
    testCase.setOutput(0.5);
    testCase.setCurrentRange(0.1, 0.5);
    testCase.setSpeedRange(500.0, 600.0);

    testCase = newTestCase();
    testCase.setOutput(1.0);
    testCase.setCurrentRange(0.6, 1.2);
    testCase.setSpeedRange(1000.0, 1100.0);
  }
}
