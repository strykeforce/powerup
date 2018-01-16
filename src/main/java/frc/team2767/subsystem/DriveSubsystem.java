package frc.team2767.subsystem;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Subsystem;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.swerve.SwerveDrive;

@Singleton
public class DriveSubsystem extends Subsystem {

  private static final Logger logger = LoggerFactory.getLogger(DriveSubsystem.class);

  private final SwerveDrive swerve;

  @Inject
  public DriveSubsystem(SwerveDrive swerve) {
    this.swerve = swerve;
  }

  @Override
  protected void initDefaultCommand() {}

  public void alignWheels() {
    swerve.saveAzimuthPositions();
    swerve.zeroAzimuthEncoders();
    String msg = "drive wheels were re-aligned";
    logger.info(msg);
    DriverStation.reportWarning(msg, false);
  }
}
