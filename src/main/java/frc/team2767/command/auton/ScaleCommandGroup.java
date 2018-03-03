package frc.team2767.command.auton;

import frc.team2767.command.intake.IntakeEject;
import frc.team2767.command.lift.LiftPosition;
import frc.team2767.command.sequence.Stow;
import frc.team2767.command.shoulder.ShoulderPosition;
import frc.team2767.subsystem.IntakeSubsystem;

public class ScaleCommandGroup extends PowerUpCommandGroup {

  public ScaleCommandGroup(Side side) {
    super();
    addSequential(new PathCommand(side.path));
    addParallel(new AzimuthCommand(side.azimuth));
    addSequential(new LiftPosition(LiftPosition.Position.SCALE_HIGH));
    addSequential(new ShoulderPosition(ShoulderPosition.Position.LAUNCH_SCALE));
    addSequential(new IntakeEject(IntakeSubsystem.Mode.FAST_EJECT));
    addSequential(new Stow());
  }

  public enum Side {
    LEFT("left_scale", 45.0),
    RIGHT("right_scale", -50.0),
    ;

    private final String path;
    private final double azimuth;

    Side(String path, double azimuth) {
      this.path = path;
      this.azimuth = azimuth;
    }
  }
}
