package frc.team2767.command.auton;

import edu.wpi.first.wpilibj.command.ConditionalCommand;
import frc.team2767.command.intake.IntakeEject;
import frc.team2767.command.shoulder.ShoulderPosition;
import frc.team2767.subsystem.IntakeSubsystem;
import openrio.powerup.MatchData.OwnedSide;

/**
 * Command group for autonomous mode.
 *
 * <p>Owned side LEFT = onTrue command, Owned side RIGHT = onFalse command
 */
public class CenterSwitchCommand extends ConditionalCommand implements OwnedSidesSettable {
  private static final double START_POSITION_YAW = 0d;

  private OwnedSide ownedSide = OwnedSide.UNKNOWN;

  public CenterSwitchCommand() {
    super(new CenterSwitchCommandGroup("C_SW_L_C1D"), new CenterSwitchCommandGroup("C_SW_R_C1D"));
  }

  @Override
  protected boolean condition() {
    return ownedSide == OwnedSide.LEFT;
  }

  @Override
  public void setOwnedSide(StartPosition startPosition, OwnedSide nearSwitch, OwnedSide scale) {
    this.ownedSide = nearSwitch;
    // don't care about scale
  }

  static class CenterSwitchCommandGroup extends PowerUpCommandGroup {

    public CenterSwitchCommandGroup(String path) {
      addParallel(new ShoulderPosition(ShoulderPosition.Position.LAUNCH_SWITCH));
      addSequential(new PathCommand(path, START_POSITION_YAW));
      addSequential(new IntakeEject(IntakeSubsystem.Mode.FAST_EJECT));
    }
  }
}
