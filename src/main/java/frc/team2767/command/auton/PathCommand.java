package frc.team2767.command.auton;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Robot;
import frc.team2767.subsystem.DriveSubsystem;

public class PathCommand extends Command {

  private final DriveSubsystem driveSubsystem = Robot.INJECTOR.driveSubsystem();
  private final String path;

  public PathCommand(String name, String path) {
    super(name);
    this.path = path;
    requires(driveSubsystem);
  }

  public PathCommand(String path) {
    this("Path", path);
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
}
