package frc.team2767.command.shoulder;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Robot;
import frc.team2767.subsystem.ShoulderSubsystem;

public class ShoulderPosition extends Command {

  private final ShoulderSubsystem shoulderSubsystem = Robot.INJECTOR.shoulderSubsystem();
  private final int position;

  public ShoulderPosition(int position) {
    this.position = position;
    requires(shoulderSubsystem);
  }

  @Override
  protected void initialize() {
    shoulderSubsystem.setPosition(position);
  }

  @Override
  protected boolean isFinished() {
    return shoulderSubsystem.onTarget();
  }
}
