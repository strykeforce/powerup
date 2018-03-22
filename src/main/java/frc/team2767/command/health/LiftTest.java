package frc.team2767.command.health;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.Robot;
import frc.team2767.command.lift.LiftZero;
import frc.team2767.subsystem.LiftSubsystem;
import frc.team2767.subsystem.health.TestCase;

import java.util.Arrays;

public class LiftTest extends CommandGroup {

  public LiftTest() {
    addSequential(new LiftZero());
    addSequential(new HealthCheckMode(true));

    addSequential(
        new FollowerVelocityTestCommand("Lift Motors", 50, Arrays.asList(51, 50)) {
          {
            setWarmUp(300);
            TestCase testCase = newTestCase();
            testCase.setOutput(0.5);
            testCase.setDuration(2000);
            testCase.setCurrentRange(1.0, 2.0);
            testCase.setSpeedRange(1000.0, 10_000.0);
          }
        });

    addSequential(new HealthCheckMode(false));
    addSequential(new LiftZero());
  }

  private static class HealthCheckMode extends InstantCommand {
    private final LiftSubsystem liftSubsystem = Robot.INJECTOR.liftSubsystem();
    private final boolean enabled;

    public HealthCheckMode(boolean enabled) {
      this.enabled = enabled;
      requires(liftSubsystem);
    }

    @Override
    protected void initialize() {
      liftSubsystem.setHealthCheckMode(enabled);
    }
  }
}
