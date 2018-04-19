package frc.team2767.command.auton;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Robot;
import frc.team2767.subsystem.DriveSubsystem;
import org.slf4j.LoggerFactory;

public class MotionDrive extends Command {
  private final double direction;
  private final int distance;
  private final double azimuth;
  private final DriveSubsystem driveSubsystem = Robot.INJECTOR.driveSubsystem();
  private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MotionDrive.class);

  public MotionDrive(double direction, int distance, double azimuth) {
    this.direction = direction;
    this.distance = distance;
    this.azimuth = azimuth;
    requires(driveSubsystem);
  }

  @Override
  protected void initialize() {
    driveSubsystem.motionTo(direction, distance, azimuth);
    driveSubsystem.resetDistance();
  }

  @Override
  protected boolean isFinished() {
    return driveSubsystem.isMotionFinished();
  }

  @Override
  protected void end() {
    driveSubsystem.endMotion();
    logger.info("MotionDrive distance = {}", driveSubsystem.getDistance());
  }
}
