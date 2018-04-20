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
  private static final double EJECT_DURATION = 0.5;
  private static final double START_POSITION_YAW = 0d;

  private static final Logger logger = LoggerFactory.getLogger(ScaleSameCube1Deliver.class);

  private final int kDistance;
  private final String settings;
  private final int kLaunchDistance;

  private final double pathDist = 7.10; // FIXME need to read from settings file
  private final double rate = 2273; // FIXME need to read from settings file
  private final double meterToIn = 39.3701;
  private final long totalDist = Math.round(pathDist * rate * meterToIn);

  ScaleSameCube1Deliver(StartPosition startPosition) {
    settings = startPosition == StartPosition.RIGHT ? "R_SC_S_C1D" : "L_SC_S_C1D";
    Toml toml = Robot.INJECTOR.settings().getAutonSettings(settings);
    String path = toml.getString("path");
    kDistance = Math.round(totalDist - toml.getLong("distance"));
    kLaunchDistance = Math.round(totalDist - toml.getLong("launchDistance"));

    PathCommand pathCommand = new PathCommand(path, startPosition.getPathAngle(START_POSITION_YAW));

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

            addSequential(new WaitForDistance(pathCommand.getPathController(), kLaunchDistance));

            addSequential(new IntakeEject(IntakeSubsystem.Mode.SCALE_EJECT, EJECT_DURATION));
          }

          @Override
          protected void end() {
            logger.trace(
                "WaitForDistance → (LiftPosition || ShoulderPosition) → IntakeEject ENDED");
          }
        });

    addSequential(pathCommand);
    // addSequential(new WaitCommand(0.50));
    // addSequential(new IntakeEject(IntakeSubsystem.Mode.SCALE_EJECT, EJECT_DURATION));
  }

  @Override
  public String toString() {
    return "ScaleSameCube1Deliver{" + "settings='" + settings + '\'' + '}';
  }
}
