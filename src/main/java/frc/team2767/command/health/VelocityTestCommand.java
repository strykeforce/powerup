package frc.team2767.command.health;

import frc.team2767.Robot;
import frc.team2767.subsystem.health.TestCase;
import frc.team2767.subsystem.health.VelocityTest;
import java.util.List;

class VelocityTestCommand extends PowerUpHealthCheckCommand {

  private final VelocityTest test;

  VelocityTestCommand(String name, List<Integer> ids) {
    test = Robot.INJECTOR.velocityTestFactory().create(name);
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
