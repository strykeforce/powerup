package frc.team2767.command.climber;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.Robot;
import frc.team2767.subsystem.ClimberSubsystem;

public class ClimberClimb extends InstantCommand {

  private final ClimberSubsystem climberSubsystem = Robot.INJECTOR.climberSubsystem();

  public ClimberClimb() {
    requires(climberSubsystem);
  }

  @Override
  protected void initialize() {
    climberSubsystem.climb();
  }
}
