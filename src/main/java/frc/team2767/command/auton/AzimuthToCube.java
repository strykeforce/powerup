package frc.team2767.command.auton;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Robot;
import frc.team2767.subsystem.DriveSubsystem;
import frc.team2767.subsystem.vision.VisionSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AzimuthToCube extends Command {

  private static final Logger logger = LoggerFactory.getLogger(AzimuthToCube.class);
  private final DriveSubsystem driveSubsystem = Robot.INJECTOR.driveSubsystem();
  private final VisionSubsystem visionSubsystem = Robot.INJECTOR.visionSubsystem();
  private boolean azimuthStarted;

  public AzimuthToCube() {
    requires(driveSubsystem);
    requires(visionSubsystem);
  }

  @Override
  protected void initialize() {
    logger.debug("Azimuth init");
    azimuthStarted = false;
    visionSubsystem.findCube();
  }

  @Override
  protected void execute() {
    if (!azimuthStarted && visionSubsystem.isFinished()) {
      double currentYaw = driveSubsystem.getGyro().getYaw();
      double setpoint = currentYaw + visionSubsystem.getCenterAngle();
      driveSubsystem.azimuthTo(setpoint);
      azimuthStarted = true;
      logger.info(
          "start vision azimuth: current yaw = {}, vision cube offset = {}",
          currentYaw,
          visionSubsystem.getCenterAngle());
    }
  }

  @Override
  protected boolean isFinished() {
    return azimuthStarted && driveSubsystem.isAzimuthFinished();
  }

  @Override
  protected void end() {
    logger.info("end vision azimuth");
    driveSubsystem.endAzimuth();
  }

  public double getCurrentYaw() {
    return driveSubsystem.getGyro().getYaw();
  }
}
