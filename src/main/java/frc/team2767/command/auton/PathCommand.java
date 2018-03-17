package frc.team2767.command.auton;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Robot;
import frc.team2767.command.StartPosition;
import frc.team2767.motion.PathController;
import frc.team2767.subsystem.DriveSubsystem;

public class PathCommand extends Command {

  private final DriveSubsystem driveSubsystem = Robot.INJECTOR.driveSubsystem();
  private final PathController path;

  public PathCommand(String name, String pathName, StartPosition startPosition) {
    super(name);
    path = Robot.INJECTOR.pathControllerFactory().create(pathName);
    switch (startPosition) {
      case UNKNOWN:
      case CENTER:
        path.setTargetAzimuth(0);
        break;
      case LEFT:
        path.setTargetAzimuth(90.0);
        break;
      case RIGHT:
        path.setTargetAzimuth(-90.0);
        break;
    }
    requires(driveSubsystem);
  }

  public PathCommand(String path, StartPosition startPosition) {
    this("Path", path, startPosition);
  }

  public PathCommand(String path) {
    this(path, StartPosition.UNKNOWN);
  }

  @Override
  protected void initialize() {
    driveSubsystem.drivePath(path);
  }

  @Override
  protected boolean isFinished() {
    return driveSubsystem.isPathFinished();
  }

  @Override
  protected void end() {
    driveSubsystem.endPath();
  }

  public PathController getPathController() {
    return path;
  }
}
