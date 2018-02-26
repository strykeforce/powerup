package frc.team2767.command.drive;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.Robot;
import frc.team2767.subsystem.DriveSubsystem;

public class UnlockAzimuthPosition extends InstantCommand {

  private final DriveSubsystem drive = Robot.INJECTOR.driveSubsystem();

  public UnlockAzimuthPosition() {
    super("Unlock");
    requires(drive);
  }

  @Override
  protected void initialize() {
    drive.disableAzimuths();
  }
}
