package frc.team2767.command.auton;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Robot;
import frc.team2767.subsystem.DriveSubsystem;
import frc.team2767.subsystem.IntakeSensorsSubsystem;
import org.strykeforce.thirdcoast.swerve.SwerveDrive;

public class DriveToCube extends Command {
  private static final double DRIVE = 0.2;

  private final DriveSubsystem driveSubsystem = Robot.INJECTOR.driveSubsystem();
  private final IntakeSensorsSubsystem intakeSensorsSubsystem =
      Robot.INJECTOR.intakeSensorsSubsystem();
  private final int targetDistance;
  private final boolean left, cross;
  int actualDistance;
  double forward, strafe;

  public DriveToCube(int targetDistance, boolean left, boolean cross) {
    this.targetDistance = targetDistance;
    this.left = left;
    this.cross = cross;
    requires(driveSubsystem);
  }

  @Override
  protected void initialize() {
    driveSubsystem.setDriveMode(SwerveDrive.DriveMode.CLOSED_LOOP);
    driveSubsystem.resetDistance();

    double angle = Math.toRadians(driveSubsystem.getGyro().getAngle());
    Cube2Fetch.logger.info(
        "driving to cube, lidar distance = {}, lidar target = {}, angle = {}",
        intakeSensorsSubsystem.getLidarDistance(),
        targetDistance,
        angle);

    if ((left && !cross) || (!left && cross)) {
      forward = DRIVE * Math.cos(angle);
      strafe = DRIVE * Math.sin(angle);
    } else {
      forward = DRIVE * Math.cos(angle);
      strafe = DRIVE * Math.sin(angle);
    }
    driveSubsystem.drive(forward, strafe, 0.0);
  }

  @Override
  protected boolean isFinished() {
    return intakeSensorsSubsystem.isLidarDisanceWithin(targetDistance);
  }

  @Override
  protected void end() {
    driveSubsystem.stop();
    actualDistance = driveSubsystem.getDistance();
    Cube2Fetch.logger.info(
        "stopped driving to cube, lidar distance = {}, actual distance = {}",
        intakeSensorsSubsystem.getLidarDistance(),
        actualDistance);
    Cube2Fetch.logger.trace("DriveToCube ENDED");
  }
}
