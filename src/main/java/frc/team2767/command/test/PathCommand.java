package frc.team2767.command.test;

import static org.strykeforce.thirdcoast.swerve.SwerveDrive.DriveMode.CLOSED_LOOP;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Robot;
import frc.team2767.motion.PathController;
import frc.team2767.subsystem.DriveSubsystem;

public class PathCommand extends Command {

  private static final String LEFT = "LEFT";
  private static final String CENTER_LEFT = "CENTERLEFT";
  private static final String CENTER_RIGHT = "CENTERRIGHT";
  private final DriveSubsystem drive;
  private PathController pathController;

  public PathCommand(int pathID) {
    drive = Robot.INJECTOR.driveSubsystem();
    requires(drive);
    if (pathID == 1) {
      pathController = new PathController(LEFT);
    } else if (pathID == 2) {
      pathController = new PathController(CENTER_LEFT);
    } else if (pathID == 3) {
      pathController = new PathController(CENTER_RIGHT);
    }
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
