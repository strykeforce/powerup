package frc.team2767.subsystem;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.team2767.command.drive.TeleOpDriveCommand;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.swerve.SwerveDrive;
import org.strykeforce.thirdcoast.swerve.SwerveDrive.DriveMode;
import org.strykeforce.thirdcoast.swerve.Wheel;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;

@Singleton
public class DriveSubsystem extends Subsystem implements Graphable {

  private static final Logger logger = LoggerFactory.getLogger(DriveSubsystem.class);
  private final SwerveDrive swerve;

  @Inject
  DriveSubsystem(SwerveDrive swerve) {
    this.swerve = swerve;
  }

  @Override
  protected void initDefaultCommand() {
    setDefaultCommand(new TeleOpDriveCommand());
  }

  public void zeroAzimuthEncoders() {
    swerve.zeroAzimuthEncoders();
  }

  public void alignWheels() {
    swerve.saveAzimuthPositions();
    swerve.zeroAzimuthEncoders();

    String msg = "drive wheels were re-aligned";
    logger.info(msg);
    DriverStation.reportWarning(msg, false);
  }

  public void setDriveMode(DriveMode mode) {
    logger.debug("setting drive mode to {}", mode);
    swerve.setDriveMode(mode);
  }

  public void drive(double forward, double strafe, double azimuth) {
    swerve.drive(forward, strafe, azimuth);
  }

  public int getDrivePosition(int wheel) {
    return swerve.getWheels()[wheel].getDriveTalon().getSelectedSensorPosition(0);
  }

  public void driveWheels(double azimuth, double drive) {
    for (Wheel wheel : swerve.getWheels()) wheel.set(azimuth, drive);
  }

  public void stop() {
    swerve.stop();
  }

  public void zeroGyro() {
    logger.warn("setting gyro yaw to zero");
    swerve.getGyro().zeroYaw();
  }

  public AHRS getGyro() {
    return swerve.getGyro();
  }

  @Override
  public void register(TelemetryService telemetryService) {
    swerve.registerWith(telemetryService);
  }
}
