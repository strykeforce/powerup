package frc.team2767.command.climber;

import edu.wpi.first.wpilibj.command.TimedCommand;
import frc.team2767.Robot;
import frc.team2767.subsystem.ClimberSubsystem;

public class ClimberRelease extends TimedCommand {

  private final ClimberSubsystem climberSubsystem = Robot.INJECTOR.climberSubsystem();

  public ClimberRelease() {
    super("Release", 0.5); // about 1/4 winch shaft rotation
    requires(climberSubsystem);
  }

  @Override
  protected void initialize() {
    climberSubsystem.release();
  }

  @Override
  protected void end() {
    climberSubsystem.stop();
  }
}
