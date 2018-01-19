package frc.team2767.command;

import edu.wpi.first.wpilibj.command.TimedCommand;
import frc.team2767.Robot;
import frc.team2767.subsystem.DriveSubsystem;
import frc.team2767.subsystem.DriveSubsystem.Mode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutonDriveCommand extends TimedCommand {

  private static final Logger logger = LoggerFactory.getLogger(AutonDriveCommand.class);
  private final DriveSubsystem drive;
  private double[] talonPositions = new double[4];

  public AutonDriveCommand() {
    super(1);
    drive = Robot.COMPONENT.driveSubsystem();
    requires(drive);
  }

  @Override
  protected void initialize() {
    drive.setDriveMode(Mode.AUTON);
    for (int i = 0; i < 4; i++) {
      talonPositions[i] = drive.getDriveTalonPos(i);
    }
    drive.driveWheels(0, 2000);
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
