package frc.team2767.command.test;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Robot;
import frc.team2767.subsystem.DriveSubsystem;
import frc.team2767.subsystem.DriveSubsystem.DriveMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Test closed loop drive-velocity mode. */
public class ClosedLoopDistTestCommand extends Command {

  private int dist;
  private DriveSubsystem drive;
  private static final Logger logger = LoggerFactory.getLogger(ClosedLoopDistTestCommand.class);

  private double[] talonPositions = new double[4];
  private double[] curTalonPositions = new double[4];

  public ClosedLoopDistTestCommand(int distance) {
    dist = distance;
    drive = Robot.COMPONENT.driveSubsystem();
    requires(drive);
  }

  @Override
  protected void initialize() {
    drive.setDriveMode(DriveMode.AUTON);
    for (int i = 0; i < 4; i++) {
      talonPositions[i] = drive.getDriveTalonPos(i);
    }
    drive.driveWheels(0, 2000);
  }

  @Override
  protected void execute() {}

  @Override
  protected void end() {
    drive.stop();
  }

  @Override
  protected boolean isFinished() {

    double sum = 0;

    for (int i = 0; i < 4; i++) {
      curTalonPositions[i] = drive.getDriveTalonPos(i);
      sum += Math.abs(curTalonPositions[i] - talonPositions[i]);
    }
    logger.debug("wheelsmoved {}", sum / 4 / 1983.65);

    return (sum / 4) / 1983.65 >= dist;
  }
}
