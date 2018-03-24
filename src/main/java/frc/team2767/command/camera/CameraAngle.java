package frc.team2767.command.camera;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Robot;
import frc.team2767.subsystem.vision.CameraSubsystem;

public class CameraAngle extends Command {
  private final CameraSubsystem cameraSubsystem = Robot.INJECTOR.cameraSubsystem();

  @Override
  protected void initialize() {
    cameraSubsystem.initialize();
  }

  @Override
  protected void end() {
    cameraSubsystem.end();
  }

  @Override
  protected boolean isFinished() {
    return cameraSubsystem.isFinished();
  }
}
