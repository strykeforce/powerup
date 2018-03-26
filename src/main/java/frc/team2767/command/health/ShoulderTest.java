package frc.team2767.command.health;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.command.shoulder.ShoulderPosition;
import frc.team2767.command.shoulder.ShoulderZeroWithEncoder;
import frc.team2767.subsystem.health.TestCase;
import java.util.Collections;

class ShoulderTest extends CommandGroup {

  public ShoulderTest() {
    addSequential(new ShoulderZeroWithEncoder());
    addSequential(new ShoulderPosition(ShoulderPosition.Position.INTAKE));

    addSequential(
        new VelocityTestCommand("Shoulder Motors", Collections.singletonList(40)) {
          {
            setWarmUp(100);
            TestCase testCase = newTestCase();
            testCase.setOutput(0.2);
            testCase.setDuration(2000);
            testCase.setCurrentRange(1.0, 2.0);
            testCase.setSpeedRange(1000.0, 10_000.0);
          }
        });
  }
}
