package frc.team2767.command.auton;

import com.moandjiezana.toml.Toml;
import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.Robot;
import frc.team2767.command.intake.IntakeEject;
import frc.team2767.command.lift.LiftPosition;
import frc.team2767.command.shoulder.ShoulderPosition;
import frc.team2767.subsystem.IntakeSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScaleSameCube1Deliver extends CommandGroup {

  private static final Logger logger = LoggerFactory.getLogger(ScaleSameCube1Deliver.class);

  private final String kPath;
  private final int kDistance;

  public ScaleSameCube1Deliver(StartPosition startPosition) {
    String settings = startPosition == StartPosition.RIGHT ? "R_SC_S_C1D" : "L_SC_S_C1D";
    Toml toml = Robot.INJECTOR.settings().getAutonSettings(settings);
    kPath = toml.getString("path");
    kDistance = toml.getLong("distance").intValue();

    PathCommand pathCommand = new PathCommand(kPath, startPosition);

    addParallel(new ShoulderPosition(ShoulderPosition.Position.TIGHT_STOW));

    addParallel(
        new CommandGroup() {
          {
            addSequential(new WaitForDistance(pathCommand.getPathController(), kDistance));

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
}
