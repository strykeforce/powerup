package frc.team2767.command.drive;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.Robot;
import frc.team2767.subsystem.DriveSubsystem;

public class ResetWheelAlignment extends InstantCommand {

  private final DriveSubsystem drive = Robot.INJECTOR.driveSubsystem();

  public ResetWheelAlignment() {
    super("Reset to Nominal");
    requires(drive);
  }

  @Override
  protected void initialize() {
    drive.resetWheelAlignmentToNominal();
  }
}
