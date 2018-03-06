package frc.team2767.command.auton;

import frc.team2767.command.StartPosition;
import frc.team2767.command.intake.IntakeEject;
import frc.team2767.command.lift.LiftPosition;
import frc.team2767.command.sequence.Stow;
import frc.team2767.command.shoulder.ShoulderPosition;
import frc.team2767.subsystem.IntakeSubsystem;

public class OppositeScaleCommandGroup extends PowerUpCommandGroup {

  public OppositeScaleCommandGroup(Side side) {
    super();
    addParallel(new ShoulderPosition(ShoulderPosition.Position.TIGHT_STOW));
    addSequential(new PathCommand(side.path, side.startPosition));
    addSequential(new AzimuthCommand(side.azimuth1));
    addSequential(new LiftPosition(LiftPosition.Position.SCALE_HIGH));
    addSequential(new ShoulderPosition(ShoulderPosition.Position.LAUNCH_SCALE));
    addSequential(new IntakeEject(IntakeSubsystem.Mode.FAST_EJECT));
    addSequential(new Stow());
  }

  public enum Side {
    LEFT("left_opposite_scale", StartPosition.LEFT, -135.0, 70.0, -1.0),
    RIGHT("right_opposite_scale", StartPosition.RIGHT, 135.0, -70.0, 1.0),
    ;

    private final String path;
    private final StartPosition startPosition;
    private final double azimuth1;
    private final double azimuth2;
    private final double sign;

    Side(String path, StartPosition startPosition, double azimuth1, double azimuth2, double sign) {
      this.path = path;
      this.startPosition = startPosition;
      this.azimuth1 = azimuth1;
      this.azimuth2 = azimuth2;
      this.sign = sign;
    }
  }
}
