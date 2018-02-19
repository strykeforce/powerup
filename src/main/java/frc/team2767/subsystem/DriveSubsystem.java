package frc.team2767.subsystem;

import static org.strykeforce.thirdcoast.swerve.SwerveDrive.DriveMode.CLOSED_LOOP;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.team2767.Settings;
import frc.team2767.command.drive.TeleOpDriveCommand;
import frc.team2767.motion.AzimuthController;
import frc.team2767.motion.AzimuthControllerFactory;
import frc.team2767.motion.PathController;
import frc.team2767.motion.PathControllerFactory;
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
  private final PathControllerFactory pathControllerFactory;
  private final AzimuthControllerFactory azimuthControllerFactory;
  private PathController pathController;
  private AzimuthController azimuthController;

  @Inject
  DriveSubsystem(
      Settings settings,
      SwerveDrive swerve,
      PathControllerFactory pathControllerFactory,
      AzimuthControllerFactory azimuthControllerFactory) {
    this.swerve = swerve;
    this.pathControllerFactory = pathControllerFactory;
    this.azimuthControllerFactory = azimuthControllerFactory;
    if (!settings.isEvent() && settings.getTable("POWERUP.AZIMUTH").getBoolean("test", false))
      azimuthController = azimuthControllerFactory.create(azimuth -> swerve.drive(0d, 0d, azimuth));
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

  public void driveWheels(double azimuth, double drive) {
    for (Wheel wheel : swerve.getWheels()) wheel.set(azimuth, drive);
  }

  //
  // PathFinder
  //
  public void drivePath(PathController pathController) {
    this.pathController = pathController;
    setDriveMode(CLOSED_LOOP);
    pathController.start();
  }

  public void drivePath(String path) {
    drivePath(pathControllerFactory.create(path));
  }

  public boolean isPathFinished() {
    return !pathController.isRunning();
  }

  public void endPath() {
    pathController.stop();
    pathController = null;
  }

  //
  // Azimuth
  //
  public void azimuthTo(double setpoint) {
    logger.info("azimuth to {}", setpoint);
    setDriveMode(CLOSED_LOOP);
    if (azimuthController == null)
      azimuthController = azimuthControllerFactory.create(azimuth -> swerve.drive(0d, 0d, azimuth));
    azimuthController.enable();
  }

  public boolean isAzimuthFinished() {
    return azimuthController.onTarget();
  }

  public void endAzimuth() {
    azimuthController.disable();
    logger.info(
        "azimuth ended setpoint = {} gyro yaw = {}",
        azimuthController.getSetpoint(),
        azimuthController.getYaw());
  }

  public int getDrivePosition(int wheel) {
    return swerve.getWheels()[wheel].getDriveTalon().getSelectedSensorPosition(0);
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
    if (azimuthController != null) telemetryService.register(azimuthController);
  }
}
