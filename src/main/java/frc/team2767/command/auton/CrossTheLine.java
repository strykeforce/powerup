package frc.team2767.command.auton;

import edu.wpi.first.wpilibj.command.TimedCommand;
import frc.team2767.Robot;
import frc.team2767.subsystem.DriveSubsystem;
import org.strykeforce.thirdcoast.swerve.SwerveDrive;

public class CrossTheLine extends PowerUpCommandGroup {

  public CrossTheLine() {
    super();
    addSequential(new DriveForward());
  }

  static class DriveForward extends TimedCommand {

    private final DriveSubsystem driveSubsystem = Robot.INJECTOR.driveSubsystem();

    public DriveForward() {
      super(5);
      requires(driveSubsystem);
    }

    @Override
    protected void initialize() {
      driveSubsystem.setDriveMode(SwerveDrive.DriveMode.OPEN_LOOP);
      driveSubsystem.driveWheels(0, 0.2);
    }
  }
}
