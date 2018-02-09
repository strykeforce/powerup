package frc.team2767.command.lift;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Robot;
import frc.team2767.subsystem.LiftSubsystem;

public class PositionCommand extends Command {

  private final LiftSubsystem liftSubsystem;
  private final int position;

  public PositionCommand(int position) {
    this.position = position;
    liftSubsystem = Robot.INJECTOR.liftSubsystem();
    requires(liftSubsystem);
  }

  @Override
  protected void initialize() {
    liftSubsystem.setSetpoint(position);
  }

  @Override
  protected boolean isFinished() {
    return liftSubsystem.onTarget();
  }
}
