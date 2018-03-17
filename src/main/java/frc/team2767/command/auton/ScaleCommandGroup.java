package frc.team2767.command.auton;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.command.StartPosition;
import frc.team2767.command.intake.IntakeEject;
import frc.team2767.command.lift.LiftPosition;
import frc.team2767.command.shoulder.ShoulderPosition;
import frc.team2767.motion.PathController;
import frc.team2767.subsystem.IntakeSubsystem;

public class ScaleCommandGroup extends PowerUpCommandGroup {

  public ScaleCommandGroup(Side side) {
    super();
    addParallel(new ShoulderPosition(ShoulderPosition.Position.TIGHT_STOW));
    PathCommand pathCommand = new PathCommand(side.path, side.startPosition);
    addParallel(new PositionForCubeLaunch(pathCommand.getPathController(), side));
    addSequential(pathCommand);
    addSequential(new AzimuthCommand(side.azimuth));
    addSequential(new IntakeEject(IntakeSubsystem.Mode.SCALE_EJECT));
  }

  public enum Side {
    LEFT("left_scale", -42.0, 500_000, StartPosition.LEFT),
    RIGHT("right_scale", 15.0, 492_400, StartPosition.RIGHT),
    ;

    private final String path;
    private final double azimuth;
    private final int distance;
    private final StartPosition startPosition;

    Side(String path, double azimuth, int distance, StartPosition startPosition) {
      this.path = path;
      this.azimuth = azimuth;
      this.distance = distance;
      this.startPosition = startPosition;
    }
  }

  private static class PositionForCubeLaunch extends CommandGroup {

    private PositionForCubeLaunch(PathController pathController, Side side) {
      addSequential(new WaitForDistance(pathController, side.distance));
      addSequential(new LiftPosition(LiftPosition.Position.SCALE_HIGH));
      addSequential(new ShoulderPosition(ShoulderPosition.Position.LAUNCH_SCALE));
    }
  }

  private static class WaitForDistance extends Command {
    private final PathController pathController;
    private final int distance;

    public WaitForDistance(PathController pathController, int distance) {
      this.pathController = pathController;
      this.distance = distance;
    }

    @Override
    protected boolean isFinished() {
      return pathController.getDistance() > distance;
    }
  }
}
