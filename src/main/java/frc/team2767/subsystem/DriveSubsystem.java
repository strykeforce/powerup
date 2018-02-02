package frc.team2767.subsystem;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.kauailabs.navx.frc.AHRS;
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
  private final TalonSRX[] driveTalons = new TalonSRX[4];

  @Inject
  DriveSubsystem(SwerveDrive swerve) {
    this.swerve = swerve;
    wheels = swerve.getWheels();

    for (int i = 0; i < 4; i++) {
      driveTalons[i] = wheels[i].getDriveTalon();
      driveTalons[i].setSelectedSensorPosition(0, 0, 0);
    }
  }

  public int getDriveTalonPos(int talonNum) {
    return driveTalons[talonNum].getSelectedSensorPosition(0);
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

  public void setDriveMode(DriveMode mode) {
    logger.debug("setting mode to {}", mode);
    switch (mode) {
      case TELEOP:
        setDriveControlMode("drive");
        break;
      case AUTON:
        setDriveControlMode("drive-velocity");
        break;
    }
  }

  private void setDriveControlMode(String mode) {
    for (Wheel w : wheels) {
      w.setDriveParameters(mode);
    }
  }

  public void drive(double forward, double strafe, double azimuth) {
    swerve.drive(forward, strafe, azimuth);
  }

  public void driveWheels(double azimuth, double drive) {
    for (Wheel w : wheels) {
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

  public AHRS getGyro() {
    return swerve.getGyro();
  }

  public enum DriveMode {
    TELEOP,
    AUTON
  }
}
