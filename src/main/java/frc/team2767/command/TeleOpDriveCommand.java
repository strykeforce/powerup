package frc.team2767.command;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Controls;
import frc.team2767.Robot;
import frc.team2767.inputadjustment.ExpoScale;
import frc.team2767.inputadjustment.RateLimit;
import frc.team2767.subsystem.DriveSubsystem;
import frc.team2767.subsystem.DriveSubsystem.DriveMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TeleOpDriveCommand extends Command {

  private static final Logger logger = LoggerFactory.getLogger(DriveSubsystem.class);
  private final DriveSubsystem drive;
  private final Controls controls;
  private ExpoScale expoScaleForward;
  private RateLimit rateLimitForward;
  private ExpoScale expoScaleStrafe;
  private RateLimit rateLimitStrafe;

  public TeleOpDriveCommand() {
    drive = Robot.COMPONENT.driveSubsystem();
    controls = Robot.COMPONENT.controls();
    rateLimitForward = new RateLimit(0.3, 0.0);
    expoScaleForward = new ExpoScale(0.05, 0);
    rateLimitStrafe = new RateLimit(0.3, 0.0);
    expoScaleStrafe = new ExpoScale(0.05, 0);
    requires(drive);
  }

  private static double applyDeadband(double input) {
    return Math.abs(input) > 0.05 ? input : 0;
  }

  @Override
  protected void initialize() {
    drive.setDriveMode(DriveMode.TELEOP);
  }

  @Override
  protected void execute() {

    double forward =
        rateLimitForward.applyRateLimit(expoScaleForward.applyExpoScale(controls.getForward()));
    double strafe =
        rateLimitStrafe.applyRateLimit(expoScaleStrafe.applyExpoScale(controls.getStrafe()));
    double azimuth = controls.getAzimuth();

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
