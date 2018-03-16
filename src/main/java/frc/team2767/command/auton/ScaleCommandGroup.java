package frc.team2767.command.auton;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;
import frc.team2767.command.StartPosition;
import frc.team2767.command.intake.IntakeEject;
import frc.team2767.command.lift.LiftPosition;
import frc.team2767.command.shoulder.ShoulderPosition;
import frc.team2767.subsystem.IntakeSubsystem;

public class ScaleCommandGroup extends PowerUpCommandGroup {

  public ScaleCommandGroup(Side side) {
    super();
    addParallel(new LiftUp(side));
    addSequential(new PathCommand(side.path, side.startPosition));

    addSequential(new AzimuthShoulderMoveCommandGroup(side));
    addSequential(new IntakeEject(IntakeSubsystem.Mode.FAST_EJECT));
    //    addSequential(new Stow());
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

  private static class AzimuthShoulderMoveCommandGroup extends CommandGroup {

    private AzimuthShoulderMoveCommandGroup(Side side) {
      addParallel(new AzimuthCommand(side.azimuth));
      addParallel(new ShoulderPosition(ShoulderPosition.Position.LAUNCH_SCALE));
    }
  }

  private static class LiftUp extends CommandGroup {
    private LiftUp(Side side) {
      addSequential(new WaitCommand(0.5));
      addSequential(new LiftPosition(LiftPosition.Position.SCALE_HIGH));
    }
  }
}
