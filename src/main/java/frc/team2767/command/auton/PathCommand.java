package frc.team2767.command.auton;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Robot;
import frc.team2767.command.StartPosition;
import frc.team2767.motion.PathController;
import frc.team2767.subsystem.DriveSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PathCommand extends Command {

  private final DriveSubsystem driveSubsystem = Robot.INJECTOR.driveSubsystem();
  private final PathController path;
  private static final Logger logger = LoggerFactory.getLogger(PathCommand.class);

  private String pathName;

  public PathCommand(String name, String pathName, StartPosition startPosition) {
    super(name);

    this.pathName = pathName;

    path = Robot.INJECTOR.pathControllerFactory().create(pathName);
    switch (startPosition) {
      case UNKNOWN:
      case CENTER:
        path.setTargetAzimuth(0);
        logger.debug("target azm = {}", 0.0);
        break;
      case LEFT:
        path.setTargetAzimuth(90.0);
        logger.debug("target azm = {}", 90.0);
        break;
      case RIGHT:
        path.setTargetAzimuth(-90.0);
        logger.debug("target azm = {}", -90.0);
        break;
    }
    requires(driveSubsystem);
  }

  // TODO: Look at designated constructor
  public PathCommand(String pathName, double azimuth) {
    path = Robot.INJECTOR.pathControllerFactory().create(pathName);
    path.setTargetAzimuth(azimuth);
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
    logger.debug("starting {}", pathName);
    driveSubsystem.drivePath(path);
  }

  @Override
  protected boolean isFinished() {
    return driveSubsystem.isPathFinished();
  }

  @Override
  protected void end() {
    logger.debug("end");
    driveSubsystem.endPath();
  }

  public PathController getPathController() {
    return path;
  }
}
