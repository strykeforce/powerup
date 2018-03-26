package frc.team2767.command.health;

import frc.team2767.subsystem.health.TestCase;
import java.util.Arrays;

class IntakeTest extends VelocityTestCommand {

  public IntakeTest() {
    super("Intake Motors", Arrays.asList(30, 31));
    TestCase testCase = newTestCase();
    testCase.setOutput(0.5);
    testCase.setCurrentRange(0.125, 0.5);
    testCase.setSpeedRange(5000.0, 6000.0);

    testCase = newTestCase();
    testCase.setOutput(1.0);
    testCase.setCurrentRange(0.5, 1.25);
    testCase.setSpeedRange(10_000.0, 12_000.0);
  }
}
