package frc.team2767.command.auton;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Robot;
import frc.team2767.command.test.ClosedLoopDistTestCommand;
import frc.team2767.subsystem.DriveSubsystem;
import frc.team2767.subsystem.DriveSubsystem.DriveMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Autonomous drive command to drive to switch from starting position.
 */
public class CrabToSwitchAutonCommand extends Command {

  private static final Logger logger = LoggerFactory.getLogger(ClosedLoopDistTestCommand.class);
  private double forw;
  private double dist;
  private double strf;
  private double azm;
  private DriveSubsystem drive;

  private double[] talonPositions = new double[4];
  private double[] curTalonPositions = new double[4];

  public CrabToSwitchAutonCommand(double forward, double strafe, double azimuth, double distance) {
    forw = forward;
    strf = strafe;
    azm = azimuth;
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
    drive.drive(forw, strf, azm);
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
