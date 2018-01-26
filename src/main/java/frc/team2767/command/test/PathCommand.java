package frc.team2767.command.test;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Robot;
import frc.team2767.motion.PathController;
import frc.team2767.subsystem.DriveSubsystem;
import frc.team2767.subsystem.DriveSubsystem.DriveMode;

public class PathCommand extends Command {

  private PathController pathController;
  private final DriveSubsystem drive;

  public PathCommand() {
    drive = Robot.COMPONENT.driveSubsystem();
    requires(drive);
    pathController = new PathController("TEST1");
  }

  @Override
  protected void initialize() {
    drive.setDriveMode(DriveMode.AUTON);
    pathController.start();
  }

  @Override
  protected boolean isFinished() {
    return !pathController.isRunning();
  }
}
