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
  private volatile double setpoint;
  private int stableCount;
  private StartPosition startPosition;
  private boolean isFinished;

  public AzimuthToCube(StartPosition startPosition) {
    this.startPosition = startPosition;
    requires(driveSubsystem);
  }

  @Override
  protected void initialize() {
    logger.debug("Azimuth init");
    stableCount = 0;
    isFinished = false;
    visionSubsystem.find(startPosition);
  }

  @Override
  protected void execute() {
    if (visionSubsystem.isFinished() && !isFinished) {
      setpoint = driveSubsystem.getGyro().getYaw();
      logger.debug(
          "vision angle => {} + {} = {}",
          visionSubsystem.getCenterAngle(),
          setpoint,
          visionSubsystem.getCenterAngle() + setpoint);
      if (visionSubsystem.getCenterAngle() < Math.abs(9)) {
        driveSubsystem.azimuthTo(visionSubsystem.getCenterAngle() + setpoint);

      } else {
        driveSubsystem.azimuthTo(setpoint);
        logger.debug("greater than 9, not azimuthing. at {}", setpoint);
      }
      isFinished = true;
    }
  }

  @Override
  protected boolean isFinished() {
    return isFinished && driveSubsystem.isAzimuthFinished();
  }

  @Override
  protected void end() {
    logger.debug("Azimuth end");
    driveSubsystem.endAzimuth();
  }

  public double getCurrentYaw() {
    return driveSubsystem.getGyro().getYaw();
  }
}
