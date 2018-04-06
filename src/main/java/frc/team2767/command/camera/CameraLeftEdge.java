package frc.team2767.command.camera;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Robot;
import frc.team2767.subsystem.vision.CameraSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CameraLeftEdge extends Command {
  private final CameraSubsystem cameraSubsystem = Robot.INJECTOR.cameraSubsystem();
  private static final Logger logger = LoggerFactory.getLogger(CameraLeftEdge.class);
  public double angle;

  @Override
  protected void initialize() {
    angle = cameraSubsystem.findLeft();
    logger.debug("Left edge: ", angle);
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
