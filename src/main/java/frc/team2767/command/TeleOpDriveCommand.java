package frc.team2767.command;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Controls;
import frc.team2767.Robot;
import frc.team2767.subsystem.DriveSubsystem;
import frc.team2767.subsystem.DriveSubsystem.Mode;

public final class TeleOpDriveCommand extends Command {

  private final DriveSubsystem drive;
  private final Controls controls;

  public TeleOpDriveCommand() {
    drive = Robot.COMPONENT.driveSubsystem();
    controls = Robot.COMPONENT.controls();
    requires(drive);
  }

  private static double applyDeadband(double input) {
    return Math.abs(input) > 0.05 ? input : 0;
  }

  @Override
  protected void initialize() {
    drive.setDriveMode(Mode.TELEOP);
  }

  @Override
  protected void execute() {
    double forward = applyDeadband(controls.getForward());
    double strafe = applyDeadband(controls.getStrafe());
    double azimuth = applyDeadband(controls.getAzimuth());

    drive.drive(forward, strafe, azimuth);
  }

  @Override
  protected boolean isFinished() {
    return false;
  }

  @Override
  protected void end() {
    drive.stop();
  }
}
