package frc.team2767.command.health;

class TearDown extends PowerUpHealthCheckCommand {

  @Override
  protected void initialize() {
    healthCheckSubsystem.saveReports();
    healthCheckSubsystem.end();
  }

  @Override
  protected boolean isFinished() {
    return true;
  }
}
