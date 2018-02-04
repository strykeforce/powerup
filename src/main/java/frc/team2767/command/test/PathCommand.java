package frc.team2767.command.test;

import static org.strykeforce.thirdcoast.swerve.SwerveDrive.DriveMode.CLOSED_LOOP;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Robot;
import frc.team2767.motion.PathController;
import frc.team2767.subsystem.DriveSubsystem;

public class PathCommand extends Command {

  private final DriveSubsystem drive;
  private PathController pathController;

  public PathCommand() {
    drive = Robot.INJECTOR.driveSubsystem();
    requires(drive);
    pathController = new PathController("TEST1");
  }

  @Override
  protected void initialize() {
    drive.setDriveMode(CLOSED_LOOP);
    pathController.start();
  }

  @Override
  protected boolean isFinished() {
    return !pathController.isRunning();
  }
}
