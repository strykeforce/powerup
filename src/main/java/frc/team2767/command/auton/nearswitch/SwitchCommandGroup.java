package frc.team2767.command.auton.nearswitch;

import frc.team2767.command.StartPosition;
import frc.team2767.command.auton.PathCommand;
import frc.team2767.command.auton.PowerUpCommandGroup;
import frc.team2767.command.intake.IntakeEject;
import frc.team2767.command.sequence.Stow;
import frc.team2767.command.shoulder.ShoulderPosition;
import frc.team2767.subsystem.IntakeSubsystem;

public class SwitchCommandGroup extends PowerUpCommandGroup {

  public SwitchCommandGroup(Side side) {
    super();

    addSequential(new PathCommand(side.path, side.startPosition));
    addSequential(new ShoulderPosition(ShoulderPosition.Position.LAUNCH_SWITCH));
    addSequential(new IntakeEject(IntakeSubsystem.Mode.FAST_EJECT));
    addSequential(new Stow());
  }

  public enum Side {
    LEFT("left_switch", StartPosition.LEFT),
    RIGHT("right_switch", StartPosition.RIGHT),
    ;

    private final String path;
    private final StartPosition startPosition;

    Side(String path, StartPosition startPosition) {
      this.path = path;
      this.startPosition = startPosition;
    }
  }
}
