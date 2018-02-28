package frc.team2767.command.auton;

import static org.strykeforce.thirdcoast.swerve.SwerveDrive.DriveMode.OPEN_LOOP;

import edu.wpi.first.wpilibj.command.ConditionalCommand;
import edu.wpi.first.wpilibj.command.TimedCommand;
import frc.team2767.Robot;
import frc.team2767.command.StartPosition;
import frc.team2767.subsystem.DriveSubsystem;
import openrio.powerup.MatchData;

/**
 * Fail-safe command, depends on starting position.
 *
 * <p>Center = onTrue command, Left or Right = onFalse command
 */
public class CrossTheLine extends ConditionalCommand implements OwnedSidesSettable {

  private StartPosition startPosition;

  public CrossTheLine() {
    super(new CenterCrossTheLine(), new DriveForward());
  }

  @Override
  protected boolean condition() {
    return startPosition == StartPosition.CENTER;
  }

  @Override
  public void setOwnedSide(
      StartPosition startPosition, MatchData.OwnedSide nearSwitch, MatchData.OwnedSide scale) {
    this.startPosition = startPosition;
  }

  static class CenterCrossTheLine extends PowerUpCommandGroup {
    public CenterCrossTheLine() {
      super();
      addSequential(new PathCommand("center_left"));
    }
  }

  static class DriveForward extends TimedCommand {

    private final DriveSubsystem driveSubsystem = Robot.INJECTOR.driveSubsystem();

    public DriveForward() {
      super(5);
      requires(driveSubsystem);
    }

    @Override
    protected void initialize() {
      driveSubsystem.setDriveMode(OPEN_LOOP);
      driveSubsystem.driveWheels(0, 0.2);
    }
  }
}
