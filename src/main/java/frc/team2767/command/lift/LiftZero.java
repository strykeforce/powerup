package frc.team2767.command.lift;

import edu.wpi.first.wpilibj.command.TimedCommand;
import frc.team2767.Robot;
import frc.team2767.subsystem.LiftSubsystem;

public class LiftZero extends TimedCommand {

  private final LiftSubsystem liftSubsystem = Robot.INJECTOR.liftSubsystem();

  public LiftZero() {
    super("Lift Zero", 5);
    requires(liftSubsystem);
  }

  @Override
  protected void initialize() {
    liftSubsystem.positionToZero();
  }

  @Override
  protected boolean isFinished() {
    return liftSubsystem.onZero();
  }

  @Override
  protected void end() {
    liftSubsystem.zeroPosition();
  }
}
