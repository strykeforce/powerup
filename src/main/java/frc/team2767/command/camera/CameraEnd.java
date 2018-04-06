package frc.team2767.command.camera;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Robot;
import frc.team2767.subsystem.vision.CameraSubsystem;

public class CameraEnd extends Command {
  private final CameraSubsystem cameraSubsystem = Robot.INJECTOR.cameraSubsystem();

  @Override
  protected void initialize() {
    cameraSubsystem.end();
  }

  @Override
  protected boolean isFinished() {
    return true;
  }
}
