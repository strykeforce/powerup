package frc.team2767.command.climber;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.Robot;
import frc.team2767.subsystem.ClimberSubsystem;

public class HoldCommand extends InstantCommand {

  private final ClimberSubsystem climberSubsystem;

  public HoldCommand() {
    climberSubsystem = Robot.INJECTOR.climberSubsystem();
    requires(climberSubsystem);
  }

  @Override
  protected void initialize() {
    climberSubsystem.hold();
  }
}
