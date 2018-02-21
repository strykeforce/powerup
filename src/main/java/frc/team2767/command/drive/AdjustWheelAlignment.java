package frc.team2767.command.drive;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.Robot;
import frc.team2767.subsystem.DriveSubsystem;

public class AdjustWheelAlignment extends InstantCommand {

  private final DriveSubsystem drive = Robot.INJECTOR.driveSubsystem();

  private final int wheel;
  private final int numTeeth;

  public AdjustWheelAlignment(String name, int wheel, int numTeeth) {
    super(name);
    requires(drive);
    this.wheel = wheel;
    this.numTeeth = numTeeth;
  }

  @Override
  protected void initialize() {
    drive.adjustWheelAlignment(wheel, numTeeth);
  }

  @Override
  protected void end() {
    drive.zeroAzimuthEncoders();
  }
}
