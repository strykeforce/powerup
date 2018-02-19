package frc.team2767.command.climber;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.Robot;
import frc.team2767.subsystem.ClimberSubsystem;

public class ClimberUnwind extends InstantCommand {

  private final ClimberSubsystem climberSubsystem = Robot.INJECTOR.climberSubsystem();

  public ClimberUnwind() {
    super("Unwind");
    requires(climberSubsystem);
  }

  @Override
  protected void initialize() {
    climberSubsystem.unwind();
  }
}
