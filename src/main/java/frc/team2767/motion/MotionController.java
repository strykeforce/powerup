package frc.team2767.motion;

import com.google.auto.factory.AutoFactory;
import com.google.auto.factory.Provided;
import edu.wpi.first.wpilibj.Notifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.swerve.SwerveDrive;

@AutoFactory
public class MotionController {
  private static final Logger logger = LoggerFactory.getLogger(MotionController.class);

  private static final int DT_MS = 20;
  private static final int T1_MS = 200;
  private static final int T2_MS = 100;
  private static final double V_PROG = 12_000 * 10; // ticks/sec

  private final double forwardComponent;
  private final double strafeComponent;
  private final SwerveDrive drive;
  private final MotionProfile motionProfile;
  private final AzimuthController azimuthController;
  private final Notifier notifier;
  private double azimuth;

  MotionController(
      double direction,
      int distance,
      double azimuth,
      @Provided SwerveDrive drive,
      @Provided AzimuthControllerFactory azimuthControllerFactory) {
    this.drive = drive;
    drive.setDriveMode(SwerveDrive.DriveMode.CLOSED_LOOP);

    double ticksPerSecMax = drive.getWheels()[0].getDriveSetpointMax() * 10.0;
    forwardComponent = Math.cos(Math.toRadians(direction)) / ticksPerSecMax;
    strafeComponent = Math.sin(Math.toRadians(direction)) / ticksPerSecMax;

    motionProfile = new MotionProfile(DT_MS, T1_MS, T2_MS, V_PROG, distance);
    azimuthController = azimuthControllerFactory.create(this::updateAzimuth);
    azimuthController.setSetpoint(azimuth);
    notifier = new Notifier(this::updateDrive);
  }

  public void start() {
    logger.info("START motion gyro angle = {}", drive.getGyro().getAngle());
    notifier.startPeriodic(DT_MS / 1000.0);
    azimuthController.enable();
  }

  public void stop() {
    logger.info("FINISH motion");
    drive.drive(0, 0, 0);
    notifier.stop();
    azimuthController.disable();
  }

  public boolean isFinished() {
    return motionProfile.isFinished() && azimuthController.onTarget();
  }

  private synchronized void updateAzimuth(double azimuth) {
    this.azimuth = azimuthController.onTarget() ? 0 : azimuth;
  }

  private void updateDrive() {
    motionProfile.calculate();
    double forward, strafe, azimuth;
    synchronized (this) {
      forward = forwardComponent * motionProfile.curr_vel;
      strafe = strafeComponent * motionProfile.curr_vel;
      azimuth = this.azimuth;
    }
    drive.drive(forward, strafe, azimuth);
  }
}
