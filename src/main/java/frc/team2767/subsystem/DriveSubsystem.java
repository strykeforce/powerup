package frc.team2767.subsystem;

import static org.strykeforce.thirdcoast.swerve.SwerveDrive.DriveMode.AZIMUTH;
import static org.strykeforce.thirdcoast.swerve.SwerveDrive.DriveMode.TRAJECTORY;

import com.kauailabs.navx.frc.AHRS;
import com.moandjiezana.toml.Toml;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.team2767.Settings;
import frc.team2767.command.auton.StartPosition;
import frc.team2767.command.drive.TeleOpDriveCommand;
import frc.team2767.motion.AzimuthController;
import frc.team2767.motion.AzimuthControllerFactory;
import frc.team2767.motion.PathController;
import frc.team2767.motion.PathControllerFactory;
import java.util.List;
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

  private static final int NUM_WHEELS = 4;
  private static final int PID = 0;

  private static final Logger logger = LoggerFactory.getLogger(DriveSubsystem.class);

  private final int kTicksPerTooth;

  private final SwerveDrive swerve;
  private final Wheel[] wheels;
  private final PathControllerFactory pathControllerFactory;
  private final AzimuthControllerFactory azimuthControllerFactory;
  private final Settings settings;
  private PathController pathController;
  private AzimuthController azimuthController;
  private StartPosition startPosition;
  private int[] start = new int[4];
  private int distanceTarget;

  @Inject
  DriveSubsystem(
      Settings settings,
      SwerveDrive swerve,
      PathControllerFactory pathControllerFactory,
      AzimuthControllerFactory azimuthControllerFactory) {
    this.swerve = swerve;
    this.pathControllerFactory = pathControllerFactory;
    this.azimuthControllerFactory = azimuthControllerFactory;
    this.settings = settings;
    kTicksPerTooth = settings.getTable("POWERUP.WHEEL").getLong("ticksPerTooth").intValue();
    if (!settings.isEvent() && settings.getTable("POWERUP.AZIMUTH").getBoolean("test", false))
      azimuthController = azimuthControllerFactory.create(azimuth -> swerve.drive(0d, 0d, azimuth));
    wheels = swerve.getWheels();
    logger.info("ticksPerTooth = {}", kTicksPerTooth);
  }

  public void resetDistance() {
    for (int i = 0; i < NUM_WHEELS; i++) {
      start[i] = wheels[i].getDriveTalon().getSelectedSensorPosition(PID);
    }
  }

  public int getDistance() {
    double distance = 0;
    for (int i = 0; i < NUM_WHEELS; i++) {
      distance += Math.abs(wheels[i].getDriveTalon().getSelectedSensorPosition(PID) - start[i]);
    }
    distance /= 4;
    return (int) distance;
  }

  public void setDistanceTarget(int distanceTarget) {
    this.distanceTarget = distanceTarget;
  }

  public boolean isDistanceTargetFinished() {
    return getDistance() >= distanceTarget;
  }

  @Override
  protected void initDefaultCommand() {
    setDefaultCommand(new TeleOpDriveCommand());
  }

  public void zeroAzimuthEncoders() {
    swerve.zeroAzimuthEncoders();
  }

  private int getNominalZero(int wheel) {
    Toml toml = settings.getTable("POWERUP.WHEEL");
    List<Long> wheels = toml.getList("nominalZero");
    return wheels.get(wheel).intValue();
  }

  public void resetWheelAlignmentToNominal() {
    Preferences prefs = Preferences.getInstance();
    for (int i = 0; i < 4; i++) {
      int position = getNominalZero(i);
      prefs.putInt(SwerveDrive.getPreferenceKeyForWheel(i), position);
      logger.warn("set wheel {} to nominal zero = {}", i, position);
    }
  }

  public void adjustWheelAlignment(int wheel, int numTeeth) {
    Preferences prefs = Preferences.getInstance();
    int nominal = getNominalZero(wheel);
    String key = SwerveDrive.getPreferenceKeyForWheel(wheel);
    int current = prefs.getInt(key, nominal);
    current += numTeeth * kTicksPerTooth;
    prefs.putInt(key, current);
    logger.info("adjusted wheel {} by {} teeth, current zero = {}", wheel, numTeeth, current);
  }

  public void alignWheelsToBar() {
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
    setDriveMode(TRAJECTORY);
    pathController.start(startPosition);
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
    setDriveMode(AZIMUTH);
    if (azimuthController == null)
      azimuthController = azimuthControllerFactory.create(azimuth -> swerve.drive(0d, 0d, azimuth));
    azimuthController.setSetpoint(setpoint);
    azimuthController.enable();
  }

  public boolean isAzimuthFinished() {
    return azimuthController.onTarget();
  }

  public void endAzimuth() {
    azimuthController.disable();
    logger.info(
        "azimuth ended setpoint = {} gyro angle = {}",
        azimuthController.getSetpoint(),
        azimuthController.getAngle());
  }

  public int getDrivePosition(int wheel) {
    return swerve.getWheels()[wheel].getDriveTalon().getSelectedSensorPosition(0);
  }

  public void stop() {
    swerve.stop();
  }

  public void zeroGyro() {
    AHRS gyro = swerve.getGyro();
    gyro.setAngleAdjustment(0);
    double adj = gyro.getAngle() % 360;
    gyro.setAngleAdjustment(-adj);
    logger.info("resetting gyro zero ({})", adj);
  }

  public void setAzimuthPosition(int position) {
    for (Wheel wheel : swerve.getWheels()) {
      wheel.setAzimuthPosition(position);
    }
  }

  public void disableAzimuths() {
    for (Wheel wheel : swerve.getWheels()) {
      wheel.disableAzimuth();
    }
  }

  public void setAngleAdjustment(StartPosition startPosition) {
    this.startPosition = startPosition;
    AHRS gyro = getGyro();
    gyro.zeroYaw();
    gyro.setAngleAdjustment(0);
    double adj = -gyro.getAngle() % 360;
    switch (startPosition) {
      case UNKNOWN:
      case CENTER:
        break;
      case LEFT:
        adj += 90d;
        gyro.setAngleAdjustment(adj);
        break;
      case RIGHT:
        adj -= 90d;
        gyro.setAngleAdjustment(adj);
        break;
    }
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
