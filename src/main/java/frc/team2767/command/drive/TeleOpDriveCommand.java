package frc.team2767.command.drive;

import static org.strykeforce.thirdcoast.swerve.SwerveDrive.DriveMode.TELEOP;

import com.moandjiezana.toml.Toml;
import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Robot;
import frc.team2767.control.DriverControls;
import frc.team2767.subsystem.DriveSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.util.ExpoScale;
import org.strykeforce.thirdcoast.util.RateLimit;

public final class TeleOpDriveCommand extends Command {

  private static final String TABLE = Robot.TABLE + ".JOYSTICK";

  private static final Logger logger = LoggerFactory.getLogger(TeleOpDriveCommand.class);

  private final double kRateLimit;

  private final DriveSubsystem drive = Robot.INJECTOR.driveSubsystem();
  private final DriverControls controls = Robot.INJECTOR.controls().getDriverControls();
  private final ExpoScale driveExpo;
  private final ExpoScale azimuthExpo;
  private RateLimit forwardRateLimit;
  private RateLimit strafeRateLimit;

  public TeleOpDriveCommand() {
    requires(drive);
    Toml toml = Robot.INJECTOR.settings().getTable(TABLE);
    double deadband = toml.getDouble("deadband");
    double driveExpo = toml.getDouble("driveExpo");
    double azimuthExpo = toml.getDouble("azimuthExpo");
    kRateLimit = toml.getDouble("rateLimit");

    this.driveExpo = new ExpoScale(deadband, driveExpo);

    this.azimuthExpo = new ExpoScale(deadband, azimuthExpo);

    logger.info("deadband = {}", deadband);
    logger.info("driveExpo = {}", driveExpo);
    logger.info("azimuthExpo = {}", azimuthExpo);
    logger.info("rateLimit = {}", kRateLimit);
  }

  @Override
  protected void initialize() {
    drive.setDriveMode(TELEOP);
    forwardRateLimit = new RateLimit(kRateLimit);
    strafeRateLimit = new RateLimit(kRateLimit);
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
