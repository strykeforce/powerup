package frc.team2767.command.test;

import static org.strykeforce.thirdcoast.swerve.SwerveDrive.DriveMode.TRAJECTORY;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Robot;
import frc.team2767.command.auton.StartPosition;
import frc.team2767.motion.PathController;
import frc.team2767.subsystem.DriveSubsystem;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;

public class PathTestCommand extends Command {

  private final DriveSubsystem driveSubsystem = Robot.INJECTOR.driveSubsystem();
  private final PathController pathController;

  public PathTestCommand(String name, String path) {
    super(name);
    pathController = Robot.INJECTOR.pathControllerFactory().create(path);
    TelemetryService telemetryService = Robot.INJECTOR.telemetryService();
    telemetryService.register(pathController);
    requires(driveSubsystem);
  }

  @Override
  protected void initialize() {
    driveSubsystem.setDriveMode(TRAJECTORY);
    pathController.start(StartPosition.CENTER);
  }

  @Override
  protected boolean isFinished() {
    return !pathController.isRunning();
  }

  @Override
  protected void end() {
    pathController.stop();
  }
}
