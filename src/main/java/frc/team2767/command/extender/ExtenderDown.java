package frc.team2767.command.extender;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.Robot;
import frc.team2767.subsystem.ExtenderSubsystem;

public class ExtenderDown extends InstantCommand {

  private final ExtenderSubsystem extenderSubsystem = Robot.INJECTOR.extenderSubsystem();

  public ExtenderDown() {
    requires(extenderSubsystem);
  }

  @Override
  protected void initialize() {
    extenderSubsystem.down();
  }
}
