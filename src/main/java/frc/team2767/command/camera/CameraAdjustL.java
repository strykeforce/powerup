package frc.team2767.command.camera;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.Robot;
import frc.team2767.command.auton.AzimuthCommand;
import frc.team2767.subsystem.vision.CameraSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CameraAdjustL extends CommandGroup {
  private final CameraSubsystem cameraSubsystem = Robot.INJECTOR.cameraSubsystem();

  private static final Logger logger = LoggerFactory.getLogger(CameraLeftEdge.class);

  protected void CameraAdjustL(double kLeftIntakeAzimuth) {
    CameraRightEdge cameraRightEdge = new CameraRightEdge();
    addSequential(cameraRightEdge);
    addSequential(new AzimuthCommand(kLeftIntakeAzimuth + cameraRightEdge.angle));
    addSequential(new CameraEnd());
  }

  @Override
  protected void end() {
    ;
  }

  @Override
  protected boolean isFinished() {
    return cameraSubsystem.isFinished();
  }
}
