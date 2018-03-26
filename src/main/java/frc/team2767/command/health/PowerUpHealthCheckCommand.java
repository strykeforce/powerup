package frc.team2767.command.health;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Robot;
import frc.team2767.subsystem.health.HealthCheckSubsystem;

abstract class PowerUpHealthCheckCommand extends Command {

  final HealthCheckSubsystem healthCheckSubsystem = Robot.INJECTOR.healthCheckSubsystem();

  PowerUpHealthCheckCommand() {
    requires(healthCheckSubsystem);
    requires(Robot.INJECTOR.driveSubsystem());
    requires(Robot.INJECTOR.climberSubsystem());
    requires(Robot.INJECTOR.intakeSubsystem());
    requires(Robot.INJECTOR.liftSubsystem());
    requires(Robot.INJECTOR.shoulderSubsystem());
  }

  @Override
  protected void interrupted() {
    healthCheckSubsystem.cancel();
  }
}
