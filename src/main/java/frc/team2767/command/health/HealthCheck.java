package frc.team2767.command.health;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Robot;
import frc.team2767.subsystem.HealthCheckSubsystem;

public class HealthCheck extends Command {

  private final HealthCheckSubsystem healthCheckSubsystem = Robot.INJECTOR.healthCheckSubsystem();

  public HealthCheck() {
    super("Health Check");
    requires(healthCheckSubsystem);
  }

  @Override
  protected void initialize() {
    healthCheckSubsystem.initialize();
  }

  @Override
  protected boolean isFinished() {
    return healthCheckSubsystem.isFinished();
  }

  @Override
  protected void interrupted() {
    healthCheckSubsystem.cancel();
    healthCheckSubsystem.end();
  }

  @Override
  protected void end() {
    healthCheckSubsystem.end();
  }
}
