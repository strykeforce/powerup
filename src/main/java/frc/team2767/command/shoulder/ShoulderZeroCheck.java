package frc.team2767.command.shoulder;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Robot;
import frc.team2767.subsystem.ShoulderSubsystem;

public class ShoulderZeroCheck extends Command {

  private final ShoulderSubsystem shoulderSubsystem = Robot.INJECTOR.shoulderSubsystem();

  public ShoulderZeroCheck() {
    super("Shoulder Zero Check");
    requires(shoulderSubsystem);
  }

  @Override
  protected void execute() {
    shoulderSubsystem.zeroPositionWithEncoderIfNeeded();
  }

  @Override
  protected boolean isFinished() {
    return false;
  }
}
