package frc.team2767.command.auton;

import edu.wpi.first.wpilibj.command.TimedCommand;
import frc.team2767.Robot;
import frc.team2767.subsystem.DriveSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimedDrive extends TimedCommand {

  private static final Logger logger = LoggerFactory.getLogger(TimedDrive.class);

  private final DriveSubsystem drive = Robot.INJECTOR.driveSubsystem();

  private final double forward;
  private final double strafe;
  private final double azimuth;

  public TimedDrive(double timeout, double forward, double strafe, double azimuth) {
    super(timeout);
    this.forward = forward;
    this.strafe = strafe;
    this.azimuth = azimuth;
    requires(drive);
  }

  @Override
  protected void initialize() {
    logger.debug("timed drive end");
    drive.drive(-forward, strafe, azimuth);
    logger.debug("forward = {}, strafe = {}, azimuth = {}", forward, strafe, azimuth);
  }
}
