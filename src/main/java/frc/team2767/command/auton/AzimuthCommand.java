package frc.team2767.command.auton;

import static frc.team2767.subsystem.DriveSubsystem.DriveMode.AUTON;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.PIDController;
import frc.team2767.Robot;
import frc.team2767.command.GraphablePIDCommand;
import frc.team2767.subsystem.DriveSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AzimuthCommand extends GraphablePIDCommand {

  private static final Logger logger = LoggerFactory.getLogger(AzimuthCommand.class);

  private static final double OUTPUT_MAX = 1;
  private static final double Kp = 0.03;
  private static final double Ki = 0;
  private static final double Kd = 0;
  private static final double TOL_DEG = 2;
  private static final int STABLE = 3;

  private final PIDController controller;
  private final DriveSubsystem drive;
  private final AHRS gyro;
  private int stableCount;

  public AzimuthCommand() {
    super("AzimuthCommand", Kp, Ki, Kd);
    controller = getPIDController();
    controller.setInputRange(-180.0, 180.0);
    controller.setContinuous(true);
    controller.setOutputRange(-OUTPUT_MAX, OUTPUT_MAX);
    controller.setAbsoluteTolerance(TOL_DEG);

    drive = Robot.COMPONENT.driveSubsystem();
    requires(drive);
    gyro = drive.getGyro();

    Robot.COMPONENT.telemetryService().register(this);
  }

  @Override
  protected void initialize() {
    drive.setDriveMode(AUTON);
    setSetpoint(0);
    stableCount = 0;
  }

  @Override
  protected double returnPIDInput() {
    return gyro.getYaw();
  }

  @Override
  protected void usePIDOutput(double azimuth) {
    drive.drive(0, 0, azimuth);
  }

  @Override
  protected boolean isFinished() {
    stableCount = controller.onTarget() ? stableCount + 1 : 0;
    return stableCount > STABLE;
  }
}
