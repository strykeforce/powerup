package frc.team2767.command.drive;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Robot;
import frc.team2767.subsystem.DriveSubsystem;

public class LockAzimuthPosition extends Command {

  private final DriveSubsystem drive = Robot.INJECTOR.driveSubsystem();

  private final int position;

  public LockAzimuthPosition(String name, int position) {
    super(name);
    this.position = position;
    requires(drive);
  }

  @Override
  protected void initialize() {
    drive.setAzimuthPosition(position);
  }

  @Override
  protected boolean isFinished() {
    return false;
  }
}
