package frc.team2767.command.auton.scale;

import edu.wpi.first.wpilibj.command.CommandGroup;
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

  public ScaleCommandGroup(ScaleSettings scaleSettings) {
    PathCommand pathCommand =
        new PathCommand(scaleSettings.getPath1(), scaleSettings.getStartPosition());

    addParallel(new ShoulderPosition(ShoulderPosition.Position.TIGHT_STOW));

    addParallel(
        new CommandGroup() {
          {
            addSequential(
                new WaitForDistance(pathCommand.getPathController(), scaleSettings.getDistance()));

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
}
