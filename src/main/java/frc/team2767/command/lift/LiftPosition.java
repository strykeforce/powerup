package frc.team2767.command.lift;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Robot;
import frc.team2767.subsystem.LiftSubsystem;

public class LiftPosition extends Command {

  private final LiftSubsystem liftSubsystem = Robot.INJECTOR.liftSubsystem();
  private final int position;

  public LiftPosition(int position) {
    this.position = position;
    requires(liftSubsystem);
  }

  @Override
  protected void initialize() {
    liftSubsystem.setPosition(position);
  }

  @Override
  protected boolean isFinished() {
    return liftSubsystem.onTarget();
  }
}
