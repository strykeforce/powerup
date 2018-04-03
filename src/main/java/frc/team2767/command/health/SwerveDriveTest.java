package frc.team2767.command.health;

import frc.team2767.subsystem.health.TestCase;
import java.util.Arrays;

class SwerveDriveTest extends VelocityTestCommand {

  public SwerveDriveTest() {
    super("Swerve Azimuth Motors", Arrays.asList(10, 11, 12, 13));
    TestCase testCase = newTestCase();
    testCase.setOutput(0.5);
    testCase.setCurrentRange(1.2, 2.0);
    testCase.setSpeedRange(18_000.0, 19_000.0);

    testCase = newTestCase();
    testCase.setOutput(1.0);
    testCase.setCurrentRange(3.875, 5.0);
    testCase.setSpeedRange(37_500.0, 39_000.0);
  }
}
