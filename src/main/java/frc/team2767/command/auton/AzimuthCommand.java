package frc.team2767.command.auton;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Robot;
import frc.team2767.subsystem.DriveSubsystem;

public class AzimuthCommand extends Command {

  private static final int STABLE = 3;

  private final DriveSubsystem driveSubsystem = Robot.INJECTOR.driveSubsystem();

  private volatile double setpoint;
  private int stableCount;

  public AzimuthCommand(double setpoint) {
    this.setpoint = setpoint;
    requires(driveSubsystem);
  }

  @Override
  protected void initialize() {
    stableCount = 0;
    driveSubsystem.azimuthTo(setpoint);
  }

  @Override
  protected boolean isFinished() {
    stableCount = driveSubsystem.isAzimuthFinished() ? stableCount + 1 : 0;
    return stableCount > STABLE;
  }

  @Override
  protected void end() {
    driveSubsystem.endAzimuth();
  }
}
