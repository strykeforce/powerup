package frc.team2767.command.drive;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Robot;
import frc.team2767.command.sensors.LidarCommand;
import frc.team2767.subsystem.DriveSubsystem;
import org.strykeforce.thirdcoast.swerve.SwerveDrive;

public class DriveToCube extends Command {

  private final DriveSubsystem driveSubsystem = Robot.INJECTOR.driveSubsystem();

  private final LidarCommand lidarCommand;

  private final double forward;
  private final double strafe;
  private final double azimuth;

  public DriveToCube(int distance, double forward, double strafe, double azimuth) {

    lidarCommand = new LidarCommand(distance);
    this.forward = forward;
    this.strafe = strafe;
    this.azimuth = azimuth;
    requires(driveSubsystem);

    driveSubsystem.setDriveMode(SwerveDrive.DriveMode.CLOSED_LOOP);
  }

  @Override
  protected void initialize() {
    driveSubsystem.drive(-forward, strafe, azimuth);
  }

  @Override
  protected boolean isFinished() {

    return lidarCommand.isInRange();
  }

  @Override
  protected void end() {
    System.out.println("Ending Drive to Cube");
    driveSubsystem.stop();
  }
}
