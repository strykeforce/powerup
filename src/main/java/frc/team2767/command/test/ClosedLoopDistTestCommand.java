package frc.team2767.command.test;

import static org.strykeforce.thirdcoast.swerve.SwerveDrive.DriveMode.CLOSED_LOOP;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Robot;
import frc.team2767.subsystem.DriveSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Test closed loop drive-velocity mode. */
public class ClosedLoopDistTestCommand extends Command {

  private static final Logger logger = LoggerFactory.getLogger(ClosedLoopDistTestCommand.class);
  private int dist;
  private DriveSubsystem drive;
  private double[] talonPositions = new double[4];
  private double[] curTalonPositions = new double[4];

  public ClosedLoopDistTestCommand(int distance) {
    dist = distance;
    drive = Robot.INJECTOR.driveSubsystem();
    requires(drive);
  }

  @Override
  protected void initialize() {
    drive.setDriveMode(CLOSED_LOOP);
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
