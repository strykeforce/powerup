package frc.team2767.command.climber;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Robot;
import frc.team2767.subsystem.ClimberSubsystem;

public class ClimberClimb extends Command {

  private final ClimberSubsystem climberSubsystem = Robot.INJECTOR.climberSubsystem();

  public ClimberClimb() {
    requires(climberSubsystem);
  }

  @Override
  protected void initialize() {
    climberSubsystem.climb();
  }

  @Override
  protected boolean isFinished() {
    return climberSubsystem.isFastClimbFinished();
  }
}
