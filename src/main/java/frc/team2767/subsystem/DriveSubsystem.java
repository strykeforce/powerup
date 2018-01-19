package frc.team2767.subsystem;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.team2767.command.TeleOpDriveCommand;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.swerve.SwerveDrive;
import org.strykeforce.thirdcoast.swerve.Wheel;

@Singleton
public class DriveSubsystem extends Subsystem {

  private static final Logger logger = LoggerFactory.getLogger(DriveSubsystem.class);

  private final SwerveDrive swerve;
  private final Wheel[] wheels;
  private final TalonSRX[] talonSRX = new TalonSRX[4];

  @Inject
  public DriveSubsystem(SwerveDrive swerve) {
    this.swerve = swerve;
    wheels = swerve.getWheels();

    for (int i = 0; i < 4; i++) {
      wheels[i].setDriveParameters("drive-velocity");
      talonSRX[i] = wheels[i].getDriveTalon();
      talonSRX[i].setSelectedSensorPosition(0, 0, 0);
    }
  }

  public int getDriveTalonPos(int talonNum) {
    return talonSRX[talonNum].getSelectedSensorPosition(0);
  }

  @Override
  protected void initDefaultCommand() {
    setDefaultCommand(new TeleOpDriveCommand());
  }

  public void alignWheels() {
    swerve.saveAzimuthPositions();
    swerve.zeroAzimuthEncoders();
    String msg = "drive wheels were re-aligned";
    logger.info(msg);
    DriverStation.reportWarning(msg, false);
  }

  public void enableTeleOp(boolean enabled) {
    logger.debug("tele-op enabled = {}", enabled);
  }

  public void drive(double forward, double strafe, double azimuth) {
    swerve.drive(forward, strafe, azimuth);
  }

  public void driveWheels(double azimuth, double drive) {
    for (Wheel w : wheels) {
      logger.debug("driveWheels set azimuth={} drive={}", azimuth, drive);
      w.set(azimuth, drive);
    }
  }

  public void stop() {
    swerve.stop();
  }

  public void zeroGyro() {
    logger.warn("setting gyro yaw to zero");
    swerve.getGyro().zeroYaw();
  }
}
