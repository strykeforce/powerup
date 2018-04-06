package frc.team2767.command.auton;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Robot;
import frc.team2767.subsystem.DriveSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AzimuthToCube extends Command {

  private static final int STABLE = 2;

  private final DriveSubsystem driveSubsystem = Robot.INJECTOR.driveSubsystem();
  private static final Logger logger = LoggerFactory.getLogger(AzimuthToCube.class);

  private volatile double setpoint;
  private int stableCount;

  public AzimuthToCube(double setpoint) {
    this.setpoint = setpoint;
    requires(driveSubsystem);
  }

  @Override
  protected void initialize() {
    logger.debug("Azimuth init");
    stableCount = 0;
    driveSubsystem.azimuthTo(setpoint);
  }

  @Override
  protected boolean isFinished() {
    stableCount = driveSubsystem.isAzimuthFinished() ? stableCount + 1 : 0;
    return stableCount > STABLE;
  }

  @Override
  protected void end() {
    logger.debug("Azimuth end");
    driveSubsystem.endAzimuth();
  }
}
