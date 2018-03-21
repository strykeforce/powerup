package frc.team2767.command.auton.scale;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Robot;
import frc.team2767.subsystem.DriveSubsystem;
import frc.team2767.subsystem.LidarSubsystem;
import org.slf4j.Logger;
import org.strykeforce.thirdcoast.swerve.SwerveDrive;

public class DriveToCube extends Command {

  static final Logger logger = ScaleCommandGroup.logger;

  private final DriveSubsystem driveSubsystem = Robot.INJECTOR.driveSubsystem();
  private final LidarSubsystem lidarSubsystem = Robot.INJECTOR.lidarSubsystem();

  private final double forward;
  private final double strafe;
  private final int distance;

  public DriveToCube(int distance, double forward, double strafe) {
    this.distance = distance;
    this.forward = forward;
    this.strafe = strafe;
    requires(lidarSubsystem);
    requires(driveSubsystem);
  }

  @Override
  protected void initialize() {
    driveSubsystem.setDriveMode(
        SwerveDrive.DriveMode.CLOSED_LOOP); // TODO: discuss - this was in constructor
    driveSubsystem.drive(-forward, strafe, 0d);
  }

  @Override
  protected boolean isFinished() {
    return lidarSubsystem.isInRange(distance);
  }

  @Override
  protected void end() {
    driveSubsystem.stop();
    logger.trace("DriveToCube ENDED");
  }
}
