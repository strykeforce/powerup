package frc.team2767.command.health;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.Robot;
import frc.team2767.command.lift.LiftZero;
import frc.team2767.subsystem.LiftSubsystem;
import frc.team2767.subsystem.health.TestCase;
import java.util.Collections;

public class LiftTest extends CommandGroup {

  public LiftTest() {
    addSequential(new LiftZero());
    addSequential(new HealthCheckMode(true));

    addSequential(
        new FollowerVelocityTestCommand("Lift Master Motor Up", 50, Collections.singletonList(50)) {
          {
            setWarmUp(300);
            TestCase testCase = newTestCase();
            testCase.setOutput(0.25);
            testCase.setDuration(2000);
            testCase.setCurrentRange(4.0, 5.25);
            testCase.setSpeedRange(300.0, 350.0);
          }
        });

    addSequential(
        new FollowerVelocityTestCommand(
            "Lift Master Motor Down", 50, Collections.singletonList(50)) {
          {
            setWarmUp(300);
            TestCase testCase = newTestCase();
            testCase.setOutput(-0.25);
            testCase.setDuration(2000);
            testCase.setCurrentRange(4.0, 5.25);
            testCase.setSpeedRange(-350.0, -300.0);
          }
        });

    addSequential(
        new FollowerVelocityTestCommand(
            "Lift Follower Motor Up", 50, Collections.singletonList(51)) {
          {
            setWarmUp(300);
            TestCase testCase = newTestCase();
            testCase.setOutput(0.25);
            testCase.setDuration(2000);
            testCase.setCurrentRange(4.0, 5.25);
            testCase.setSpeedRange(300.0, 350.0);
          }
        });

    addSequential(
        new FollowerVelocityTestCommand(
            "Lift Follower Motor Down", 50, Collections.singletonList(51)) {
          {
            setWarmUp(300);
            TestCase testCase = newTestCase();
            testCase.setOutput(-0.25);
            testCase.setDuration(2000);
            testCase.setCurrentRange(4.0, 5.25);
            testCase.setSpeedRange(-350.0, -300.0);
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
