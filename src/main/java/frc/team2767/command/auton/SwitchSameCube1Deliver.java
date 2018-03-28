package frc.team2767.command.auton;

import com.moandjiezana.toml.Toml;
import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.Robot;
import frc.team2767.command.intake.IntakeEject;
import frc.team2767.command.shoulder.ShoulderPosition;
import frc.team2767.subsystem.IntakeSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SwitchSameCube1Deliver extends CommandGroup {
  private static final double START_POSITION_YAW = 0d;
  private static final double EJECT_DURATION = 0.5;

  private static final Logger logger = LoggerFactory.getLogger(SwitchSameCube1Deliver.class);
  private final int kDistance;
  private final String settings;

  public SwitchSameCube1Deliver(StartPosition startPosition) {
    settings = startPosition == StartPosition.RIGHT ? "R_SW_S_C1D" : "L_SW_S_C1D";
    Toml toml = Robot.INJECTOR.settings().getAutonSettings(settings);
    String path = toml.getString("path");
    kDistance = toml.getLong("distance").intValue();

    PathCommand pathCommand = new PathCommand(path, startPosition.getPathAngle(START_POSITION_YAW));

    addParallel(new ShoulderPosition(ShoulderPosition.Position.TIGHT_STOW));

    addParallel(
        new CommandGroup() {
          {
            addSequential(new WaitForDistance(pathCommand.getPathController(), kDistance));

            addSequential(
                new CommandGroup() {
                  {
                    addParallel(new ShoulderPosition(ShoulderPosition.Position.LAUNCH_SWITCH));
                  }

                  @Override
                  protected void end() {
                    logger.trace("LiftPosition || ShoulderPosition ENDED");
                  }
                });

            addSequential(new IntakeEject(IntakeSubsystem.Mode.SWITCH_EJECT, EJECT_DURATION));
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
  public String toString() {
    return "SwitchSameCube1Deliver{" + "settings='" + settings + '\'' + '}';
  }
}
