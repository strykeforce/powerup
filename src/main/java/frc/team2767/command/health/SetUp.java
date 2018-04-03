package frc.team2767.command.health;

class SetUp extends PowerUpHealthCheckCommand {

  @Override
  protected void initialize() {
    healthCheckSubsystem.initialize();
  }

  @Override
  protected boolean isFinished() {
    return true;
  }
}
