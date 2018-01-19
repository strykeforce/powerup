package frc.team2767.command;

import edu.wpi.first.wpilibj.command.TimedCommand;
import frc.team2767.Robot;
import frc.team2767.subsystem.DriveSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.swerve.Wheel;

public class AutonDriveCommand extends TimedCommand {

  private static final Logger logger = LoggerFactory.getLogger(AutonDriveCommand.class);
  private final DriveSubsystem drive;
  private final Wheel[] wheels = new Wheel[4];
  private double[] talonPositions = new double[4];

  public AutonDriveCommand() {
    super(1);
    drive = Robot.COMPONENT.driveSubsystem();
    requires(drive);
  }

  @Override
  protected void execute() {
    for (int i = 0; i < 4; i++) {
      logger.debug("AutonDrive encoder{} position={}", i, drive.getDriveTalonPos(i));
    }
  }

  @Override
  protected void initialize() {
    for (int i = 0; i < 4; i++) {
      talonPositions[i] = drive.getDriveTalonPos(i);
    }
  }

  @Override
  protected void end() {
    for (int i = 0; i < 4; i++) {
      logger.debug(
          "wheel {} moved {}", i, (talonPositions[i] - drive.getDriveTalonPos(i)) / 1983.65);
    }
    drive.stop();
  }
}
