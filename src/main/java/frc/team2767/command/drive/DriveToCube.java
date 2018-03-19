package frc.team2767.command.drive;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Robot;
import frc.team2767.subsystem.DriveSubsystem;
import frc.team2767.subsystem.LidarSubsystem;
import org.strykeforce.thirdcoast.swerve.SwerveDrive;

public class DriveToCube extends Command {

  private final DriveSubsystem driveSubsystem = Robot.INJECTOR.driveSubsystem();
  private final LidarSubsystem lidarSubsystem = Robot.INJECTOR.lidarSubsystem();

  private final double forward;
  private final double strafe;
  private final double azimuth;
  private final int distance;

  public DriveToCube(int distance, double forward, double strafe, double azimuth) {
    this.distance = distance;
    this.forward = forward;
    this.strafe = strafe;
    this.azimuth = azimuth;
    requires(lidarSubsystem);
    requires(driveSubsystem);

    driveSubsystem.setDriveMode(SwerveDrive.DriveMode.CLOSED_LOOP);
  }

  @Override
  protected void initialize() {
    driveSubsystem.drive(-forward, strafe, azimuth);
  }

  @Override
  protected boolean isFinished() {

    return lidarSubsystem.isInRange(distance);
  }

  @Override
  protected void end() {
    System.out.println("Ending Drive to Cube");
    driveSubsystem.stop();
  }
}
