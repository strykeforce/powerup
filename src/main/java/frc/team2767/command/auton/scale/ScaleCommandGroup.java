package frc.team2767.command.auton.scale;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.command.StartPosition;
import frc.team2767.command.auton.PathCommand;
import frc.team2767.command.auton.WaitForDistance;
import frc.team2767.command.intake.IntakeEject;
import frc.team2767.command.lift.LiftPosition;
import frc.team2767.command.shoulder.ShoulderPosition;
import frc.team2767.subsystem.IntakeSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScaleCommandGroup extends CommandGroup {
  static final Logger logger = LoggerFactory.getLogger(ScaleCommandGroup.class);

  public ScaleCommandGroup(Side side) {
    PathCommand pathCommand = new PathCommand(side.path, side.startPosition);

    addParallel(new ShoulderPosition(ShoulderPosition.Position.TIGHT_STOW));

    addParallel(
        new CommandGroup() {
          {
            addSequential(new WaitForDistance(pathCommand.getPathController(), side.distance));

            addSequential(
                new CommandGroup() {
                  {
                    addParallel(new LiftPosition(LiftPosition.Position.SCALE_HIGH));
                    addParallel(new ShoulderPosition(ShoulderPosition.Position.LAUNCH_SCALE));
                  }

                  @Override
                  protected void end() {
                    logger.trace("LiftPosition || ShoulderPosition ENDED");
                  }
                });

            addSequential(new IntakeEject(IntakeSubsystem.Mode.SCALE_EJECT));
          }

          @Override
          protected void end() {
            logger.trace(
                "WaitForDistance → (LiftPosition || ShoulderPosition) → IntakeEject ENDED");
          }
        });

    addSequential(pathCommand);
  }

  @Override
  protected void end() {
    logger.trace("ScaleCommandGroup ENDED");
  }

  public enum Side {
    LEFT("left_scale", 492_400, StartPosition.LEFT),
    RIGHT("right_scale", 210_000, StartPosition.RIGHT),
    ;

    private final String path;
    private final int distance;
    private final StartPosition startPosition;

    Side(String path, int distance, StartPosition startPosition) {
      this.path = path;
      this.distance = distance;
      this.startPosition = startPosition;
    }
  }
}
