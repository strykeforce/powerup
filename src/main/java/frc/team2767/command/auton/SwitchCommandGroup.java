package frc.team2767.command.auton;

import frc.team2767.command.StartPosition;
import frc.team2767.command.intake.IntakeEject;
import frc.team2767.command.sequence.Stow;
import frc.team2767.command.shoulder.ShoulderPosition;
import frc.team2767.subsystem.IntakeSubsystem;

public class SwitchCommandGroup extends PowerUpCommandGroup {

  public SwitchCommandGroup(Side side) {
    super();
    addParallel(new ShoulderPosition(ShoulderPosition.Position.LAUNCH_SWITCH));

    addSequential(new PathCommand(side.path, side.startPosition));

    addSequential(new IntakeEject(IntakeSubsystem.Mode.FAST_EJECT));
    addSequential(new Stow());
  }

  public enum Side {
    LEFT("left_switch", 90.0, StartPosition.LEFT),
    RIGHT("right_switch", -90.0, StartPosition.RIGHT),
    ;

    private final String path;
    private final double azimuth;
    private final StartPosition startPosition;

    Side(String path, double azimuth, StartPosition startPosition) {
      this.path = path;
      this.azimuth = azimuth;
      this.startPosition = startPosition;
    }
  }
}
