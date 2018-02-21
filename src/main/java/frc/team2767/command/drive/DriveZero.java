package frc.team2767.command.drive;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Robot;
import frc.team2767.subsystem.DriveSubsystem;
import org.strykeforce.thirdcoast.swerve.SwerveDrive;

public class DriveZero extends Command {

  private final DriveSubsystem drive = Robot.INJECTOR.driveSubsystem();
  private final double output;

  public DriveZero(double output) {
    super("Drive Zero");
    this.output = output;
    requires(drive);
  }

  @Override
  protected void initialize() {
    drive.setDriveMode(SwerveDrive.DriveMode.CLOSED_LOOP);
    drive.driveWheels(0, output);
  }

  @Override
  protected boolean isFinished() {
    return false;
  }

  @Override
  protected void end() {
    drive.driveWheels(0, 0);
  }
}
