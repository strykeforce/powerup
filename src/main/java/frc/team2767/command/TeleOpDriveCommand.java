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
  private ExpoScale expoScale;
  private RateLimit rateLimit;

  public TeleOpDriveCommand() {
    drive = Robot.COMPONENT.driveSubsystem();
    controls = Robot.COMPONENT.controls();
    rateLimit = new RateLimit(0.3, 0.0);
    expoScale = new ExpoScale(0.05, 0);
    requires(drive);
  }

  private static double applyDeadband(double input) {
    return Math.abs(input) > 0.05 ? input : 0;
  }

  private double applyInputAdjustments(double input) {
    if (rateLimit.applyRateLimit(expoScale.applyExpoScale(input)) != 0.0) {
      logger.debug(
          "Premath={} Postmath={}",
          input,
          rateLimit.applyRateLimit(expoScale.applyExpoScale(input)));
    }
    return rateLimit.applyRateLimit(expoScale.applyExpoScale(input));
  }

  @Override
  protected void initialize() {
    drive.setDriveMode(DriveMode.TELEOP);
  }

  @Override
  protected void execute() {
    //    double forward = applyDeadband(controls.getForward());
    //    double strafe = applyDeadband(controls.getStrafe());
    //    double azimuth = applyDeadband(controls.getAzimuth());

    double forward = applyInputAdjustments(controls.getForward());
    double strafe = applyInputAdjustments(controls.getStrafe());
    double azimuth = applyInputAdjustments(controls.getAzimuth());

    //    double forward = applyInputAdjustments(controls.getForward());
    //    double strafe = applyInputAdjustments(controls.getStrafe());
    //    double azimuth = controls.getAzimuth();

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
