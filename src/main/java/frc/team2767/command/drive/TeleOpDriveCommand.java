package frc.team2767.command.drive;

import static org.strykeforce.thirdcoast.swerve.SwerveDrive.DriveMode.OPEN_LOOP;

import com.moandjiezana.toml.Toml;
import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Controls;
import frc.team2767.Robot;
import frc.team2767.subsystem.DriveSubsystem;
import org.strykeforce.thirdcoast.util.ExpoScale;
import org.strykeforce.thirdcoast.util.RateLimit;

public final class TeleOpDriveCommand extends Command {

  private static final String TABLE = Robot.TABLE + ".JOYSTICK";

  //  private static final Logger logger = LoggerFactory.getLogger(DriveSubsystem.class);
  private final DriveSubsystem drive;
  private final Controls controls;
  private final double kJoystickDeadband;
  private final double kExpoScale;
  private final double kRateLimit;
  private ExpoScale expoScaleForward;
  private RateLimit rateLimitForward;
  private ExpoScale expoScaleStrafe;
  private RateLimit rateLimitStrafe;

  public TeleOpDriveCommand() {
    drive = Robot.INJECTOR.driveSubsystem();
    controls = Robot.INJECTOR.controls();
    Toml toml = Robot.INJECTOR.settings().getTable(TABLE);
    kJoystickDeadband = toml.getDouble("deadband", 0.05);
    kExpoScale = toml.getDouble("expoScale", 0.5);
    kRateLimit = toml.getDouble("rateLimit", 0.0);

    rateLimitForward = new RateLimit(kRateLimit, 0.0);
    expoScaleForward = new ExpoScale(kJoystickDeadband, kExpoScale);
    rateLimitStrafe = new RateLimit(kRateLimit, 0.0);
    expoScaleStrafe = new ExpoScale(kJoystickDeadband, kExpoScale);
    requires(drive);
  }

  private static double applyDeadband(double input) {
    return Math.abs(input) > 0.05 ? input : 0;
  }

  @Override
  protected void initialize() {
    drive.setDriveMode(OPEN_LOOP);
  }

  @Override
  protected void execute() {
    double forward =
        rateLimitForward.applyRateLimit(expoScaleForward.applyExpoScale(controls.getForward()));
    double strafe =
        rateLimitStrafe.applyRateLimit(expoScaleStrafe.applyExpoScale(controls.getStrafe()));

    double azimuth = applyDeadband(controls.getAzimuth());

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
