package frc.team2767.command.health;

import frc.team2767.Robot;
import frc.team2767.subsystem.health.TestCase;
import frc.team2767.subsystem.health.VelocityTest;
import java.util.List;

class FollowerVelocityTestCommand extends PowerUpHealthCheckCommand {

  private final VelocityTest test;

  FollowerVelocityTestCommand(String name, int masterId, List<Integer> ids) {
    test = Robot.INJECTOR.followerVelocityTestFactory().create(name, masterId);
    test.addAllIds(ids);
  }

  void setWarmUp(long millis) {
    test.setWarmup(millis);
  }

  TestCase newTestCase() {
    return test.newTestCase();
  }

  @Override
  protected void initialize() {
    healthCheckSubsystem.runTest(test);
  }

  @Override
  protected boolean isFinished() {
    return healthCheckSubsystem.isFinished();
  }
}
