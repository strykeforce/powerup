package frc.team2767.command.drive;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.Robot;
import frc.team2767.subsystem.DriveSubsystem;

public class ZeroGyroYawCommand extends InstantCommand {

  private final DriveSubsystem drive;

  public ZeroGyroYawCommand() {
    drive = Robot.INJECTOR.driveSubsystem();
    requires(drive);
  }

  @Override
  protected void initialize() {
    drive.zeroGyro();
  }
}
