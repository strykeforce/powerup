package frc.team2767.command.auton.scale;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;
import frc.team2767.command.StartPosition;
import frc.team2767.command.auton.AzimuthCommand;
import frc.team2767.command.auton.PathCommand;
import frc.team2767.command.intake.IntakeEject;
import frc.team2767.command.intake.IntakeLoad;
import frc.team2767.command.intake.StartIntakeHold;
import frc.team2767.command.lift.LiftPosition;
import frc.team2767.command.sequence.Stow;
import frc.team2767.command.shoulder.ShoulderPosition;
import frc.team2767.subsystem.IntakeSubsystem;
import org.slf4j.Logger;

// FIXME: this probably can be generalized like ScaleCommandGroup
public class ScaleSecondCubeRightCommandGroup extends CommandGroup {

  static final Logger logger = ScaleCommandGroup.logger;

  private final int DRIVE_STOP_DISTANCE = 44;
  private final int INTAKE_STOP_DISTANCE = 35;

  public ScaleSecondCubeRightCommandGroup() {

    addSequential(
        new CommandGroup() {
          {
            addSequential(
                new CommandGroup() {
                  {
                    addParallel(new Stow(), 1.0);
                    addParallel(new PathCommand("right_secondcube_1", StartPosition.RIGHT));
                  }
                });

            addSequential(
                new CommandGroup() {
                  {
                    addParallel(new AzimuthCommand(-50.0));
                    addParallel(new IntakeLoad(IntakeLoad.Position.GROUND), 0.25);
                  }
                });
          }

          @Override
          protected void end() {
            logger.trace("(Stow || PathCommand) → (AzimuthCommand || IntakeLoad) ENDED");
          }
        });

    addSequential(
        new CommandGroup() {
          {
            addParallel(new IntakeInCubeTwo(INTAKE_STOP_DISTANCE), 3.0);
            addParallel(new DriveToCube(DRIVE_STOP_DISTANCE, 0.2, -0.2));
          }

          @Override
          protected void end() {
            logger.trace("IntakeInCubeTwo || DriveToCube ENDED");
          }
        });

    addSequential(
        new CommandGroup() {
          {
            addSequential(new StartIntakeHold());
            addParallel(new PathCommand("right_secondcube_2", -130.0));
            addSequential(new WaitCommand(0.5));
            addSequential(new ShoulderPosition(ShoulderPosition.Position.TIGHT_STOW));
          }

          @Override
          protected void end() {
            logger.trace(
                "StartIntakeHold → (PathCommand || (WaitCommand → ShoulderPosition)) ENDED");
          }
        });

    addSequential(
        new CommandGroup() {
          {
            addParallel(new AzimuthCommand(20.0));
            addParallel(new LiftPosition(LiftPosition.Position.SCALE_HIGH));
          }

          @Override
          protected void end() {
            logger.trace("AzimuthCommand || LiftPosition ENDED");
          }
        });

    addSequential(new ShoulderPosition(ShoulderPosition.Position.LAUNCH_SCALE));
    addSequential(new IntakeEject(IntakeSubsystem.Mode.SCALE_EJECT));
  }

  @Override
  protected void end() {
    logger.trace("ScaleSecondCubeRightCommandGroup ENDED");
  }
}
