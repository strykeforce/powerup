package frc.team2767.command.shoulder;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.Robot;
import frc.team2767.subsystem.ShoulderSubsystem;

public class Down extends InstantCommand {

  private final ShoulderSubsystem shoulderSubsystem;

  public Down() {
    shoulderSubsystem = Robot.INJECTOR.shoulderSubsystem();
    requires(shoulderSubsystem);
  }

  @Override
  protected void initialize() {
    shoulderSubsystem.down();
  }
}
