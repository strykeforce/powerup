package frc.team2767.command.shoulder;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.Robot;
import frc.team2767.subsystem.ShoulderSubsystem;

public class ShoulderZeroWithEncoder extends InstantCommand {

  private final ShoulderSubsystem shoulderSubsystem = Robot.INJECTOR.shoulderSubsystem();

  public ShoulderZeroWithEncoder() {
    super("Shoulder Enc Zero");
    requires(shoulderSubsystem);
  }

  @Override
  protected void initialize() {
    shoulderSubsystem.zeroPositionWithEncoder();
  }
}
