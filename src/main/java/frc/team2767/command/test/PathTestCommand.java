package frc.team2767.command.test;

import static org.strykeforce.thirdcoast.swerve.SwerveDrive.DriveMode.CLOSED_LOOP;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Robot;
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
    driveSubsystem.setDriveMode(CLOSED_LOOP);
    pathController.start();
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
