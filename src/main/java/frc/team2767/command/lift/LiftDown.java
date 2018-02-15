package frc.team2767.command.lift;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.Robot;
import frc.team2767.subsystem.LiftSubsystem;

public class LiftDown extends InstantCommand {

  private final LiftSubsystem liftSubsystem = Robot.INJECTOR.liftSubsystem();

  public LiftDown() {
    requires(liftSubsystem);
  }

  @Override
  protected void initialize() {
    liftSubsystem.down();
  }
}
