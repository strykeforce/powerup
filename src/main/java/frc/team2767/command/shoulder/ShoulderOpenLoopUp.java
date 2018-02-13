package frc.team2767.command.shoulder;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.Robot;
import frc.team2767.subsystem.ShoulderSubsystem;

public class ShoulderOpenLoopUp extends InstantCommand {

  private final ShoulderSubsystem shoulderSubsystem;

  public ShoulderOpenLoopUp() {
    shoulderSubsystem = Robot.INJECTOR.shoulderSubsystem();
    requires(shoulderSubsystem);
  }

  @Override
  protected void initialize() {
    shoulderSubsystem.openLoopUp();
  }
}