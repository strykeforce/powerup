package frc.team2767.command.shoulder;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Robot;
import frc.team2767.subsystem.ShoulderSubsystem;

public class ShoulderZeroWithLimitSwitch extends Command {

  private final ShoulderSubsystem shoulderSubsystem = Robot.INJECTOR.shoulderSubsystem();

  public ShoulderZeroWithLimitSwitch() {
    super("Shoulder LS Zero");
    requires(shoulderSubsystem);
  }

  @Override
  protected void initialize() {
    shoulderSubsystem.positionToZero();
  }

  @Override
  protected boolean isFinished() {
    return shoulderSubsystem.onZero();
  }

  @Override
  protected void end() {
    shoulderSubsystem.zeroPositionWithLimitSwitch();
  }
}
