package frc.team2767.command.auton;

import frc.team2767.command.StartPosition;
import frc.team2767.command.intake.IntakeEject;
import frc.team2767.command.lift.LiftPosition;
import frc.team2767.command.sequence.Stow;
import frc.team2767.command.shoulder.ShoulderPosition;
import frc.team2767.subsystem.IntakeSubsystem;

public class ScaleCommandGroup extends PowerUpCommandGroup {

  public ScaleCommandGroup(Side side) {
    super();
    addSequential(new PathCommand(side.path, side.startPosition));
    addParallel(new AzimuthCommand(side.azimuth));
    addSequential(new LiftPosition(LiftPosition.Position.SCALE_HIGH));
    addSequential(new ShoulderPosition(ShoulderPosition.Position.LAUNCH_SCALE));
    addSequential(new IntakeEject(IntakeSubsystem.Mode.FAST_EJECT));
    addSequential(new Stow());
  }

  public enum Side {
    LEFT("left_scale", -42.0, StartPosition.LEFT),
    RIGHT("right_scale", 40.0, StartPosition.RIGHT),
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
