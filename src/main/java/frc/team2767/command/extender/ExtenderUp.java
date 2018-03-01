package frc.team2767.command.extender;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.Robot;
import frc.team2767.subsystem.ExtenderSubsystem;

public class ExtenderUp extends InstantCommand {

  private final ExtenderSubsystem extenderSubsystem = Robot.INJECTOR.extenderSubsystem();

  public ExtenderUp() {
    requires(extenderSubsystem);
  }

  @Override
  protected void initialize() {
    extenderSubsystem.up();
  }
}
