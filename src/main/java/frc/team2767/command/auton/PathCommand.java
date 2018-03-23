package frc.team2767.command.auton;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Robot;
import frc.team2767.command.StartPosition;
import frc.team2767.motion.PathController;
import frc.team2767.subsystem.DriveSubsystem;

public class PathCommand extends Command {

  private final DriveSubsystem driveSubsystem = Robot.INJECTOR.driveSubsystem();
  private final PathController path;

  public PathCommand(String name, String pathName, double azimuth) {
    super(name);
    path = Robot.INJECTOR.pathControllerFactory().create(pathName);
    path.setTargetAzimuth(azimuth);
    requires(driveSubsystem);
  }

  public PathCommand(String pathName, double azimuth) {
    this("PathCommand", pathName, azimuth);
  }

  public PathCommand(String name, String pathName, StartPosition startPosition) {
    this(name, pathName, targetAzimuth(startPosition));
  }

  public PathCommand(String path, StartPosition startPosition) {
    this("PathCommand", path, startPosition);
  }

  public PathCommand(String path) {
    this(path, StartPosition.UNKNOWN);
  }

  private static double targetAzimuth(StartPosition startPosition) {
    switch (startPosition) {
      case LEFT:
        return 90d;
      case RIGHT:
        return -90d;
      default:
        return 0d;
    }
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
