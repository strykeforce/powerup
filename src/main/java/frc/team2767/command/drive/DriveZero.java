package frc.team2767.command.drive;

import static org.strykeforce.thirdcoast.swerve.SwerveDrive.DriveMode.CLOSED_LOOP;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Robot;
import frc.team2767.subsystem.DriveSubsystem;

public class DriveZero extends Command {

  private final DriveSubsystem drive = Robot.INJECTOR.driveSubsystem();
  private final double output;

  public DriveZero(String name, double output) {
    super(name);
    this.output = output;
    requires(drive);
  }

  @Override
  protected void initialize() {
    drive.setDriveMode(CLOSED_LOOP);
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
