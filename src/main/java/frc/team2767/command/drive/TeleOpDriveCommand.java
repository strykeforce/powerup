package frc.team2767.command.drive;

import com.moandjiezana.toml.Toml;
import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Robot;
import frc.team2767.control.DriverControls;
import frc.team2767.subsystem.DriveSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.util.ExpoScale;
import org.strykeforce.thirdcoast.util.RateLimit;

import static org.strykeforce.thirdcoast.swerve.SwerveDrive.DriveMode.OPEN_LOOP;

public final class TeleOpDriveCommand extends Command {

  private static final String TABLE = Robot.TABLE + ".JOYSTICK";

  private static final Logger logger = LoggerFactory.getLogger(TeleOpDriveCommand.class);
  private final DriveSubsystem drive;
  private final DriverControls controls;
  private final ExpoScale driveExpo;
  private final ExpoScale azimuthExpo;
  private final RateLimit forwardRateLimit;
  private final RateLimit strafeRateLimit;

  public TeleOpDriveCommand() {
    drive = Robot.INJECTOR.driveSubsystem();
    requires(drive);

    controls = Robot.INJECTOR.controls().getDriverControls();

    Toml toml = Robot.INJECTOR.settings().getTable(TABLE);
    double kDeadband = toml.getDouble("deadband");
    double kDriveExpo = toml.getDouble("driveExpo");
    double kAzimuthExpo = toml.getDouble("azimuthExpo");
    double kRateLimit = toml.getDouble("rateLimit");

    forwardRateLimit = new RateLimit(kRateLimit);
    driveExpo = new ExpoScale(kDeadband, kDriveExpo);
    strafeRateLimit = new RateLimit(kRateLimit);
    azimuthExpo = new ExpoScale(kDeadband, kAzimuthExpo);

    logger.info("deadband = {}", kDeadband);
    logger.info("driveExpo = {}", kDriveExpo);
    logger.info("azimuthExpo = {}", kAzimuthExpo);
    logger.info("rateLimit = {}", kRateLimit);
  }

  @Override
  protected void initialize() {
    drive.setDriveMode(OPEN_LOOP);
  }

  @Override
  protected void execute() {
    double forward = forwardRateLimit.apply(driveExpo.apply(controls.getForward()));
    double strafe = strafeRateLimit.apply(driveExpo.apply(controls.getStrafe()));
    double azimuth = azimuthExpo.apply(controls.getAzimuth());

    drive.drive(forward, strafe, azimuth);
  }

  @Override
  protected boolean isFinished() {
    return false;
  }

  @Override
  protected void end() {
    drive.stop();
  }
}
