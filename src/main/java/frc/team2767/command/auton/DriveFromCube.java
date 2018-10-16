package frc.team2767.command.auton;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Robot;
import frc.team2767.subsystem.DriveSubsystem;
import org.strykeforce.thirdcoast.swerve.SwerveDrive;

public class DriveFromCube extends Command {
  private final DriveSubsystem driveSubsystem = Robot.INJECTOR.driveSubsystem();
  private double multiplier = 2.0;

  private final DriveToCube driveToCube;

  public DriveFromCube(DriveToCube driveToCube) {
    this(driveToCube, 2.0);
  }

  public DriveFromCube(DriveToCube driveToCube, double multiplier) {
    this.multiplier = multiplier;
    this.driveToCube = driveToCube;
    requires(driveSubsystem);
  }

  @Override
  protected void initialize() {
    driveSubsystem.setDriveMode(SwerveDrive.DriveMode.CLOSED_LOOP);
    driveSubsystem.resetDistance();
    driveSubsystem.setDistanceTarget(driveToCube.actualDistance);
    driveSubsystem.drive(-multiplier * driveToCube.forward, -multiplier * driveToCube.strafe, 0.0);
  }

  @Override
  protected boolean isFinished() {
    return driveSubsystem.isDistanceTargetFinished();
  }
}
