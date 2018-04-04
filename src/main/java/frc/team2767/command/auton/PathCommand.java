package frc.team2767.command.auton;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Robot;
import frc.team2767.motion.PathController;
import frc.team2767.subsystem.DriveSubsystem;

public class PathCommand extends Command {

  private final DriveSubsystem driveSubsystem = Robot.INJECTOR.driveSubsystem();
  private final PathController path;
  private boolean autoPathAzimuth;

  PathCommand(String pathName, double azimuth) {
    path = Robot.INJECTOR.pathControllerFactory().create(pathName);
    path.setTargetAzimuth(azimuth);
    requires(driveSubsystem);
  }

  public PathCommand(String pathName) {
    this(pathName, 0d);
    autoPathAzimuth = true;
  }

  @Override
  protected void initialize() {
    if (autoPathAzimuth)
      path.setTargetAzimuth(Math.IEEEremainder(driveSubsystem.getGyro().getAngle(), 360.0));
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
