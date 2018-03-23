package frc.team2767.command.auton.scale;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;
import frc.team2767.command.auton.AzimuthCommand;
import frc.team2767.command.auton.PathCommand;
import frc.team2767.command.intake.*;
import frc.team2767.command.lift.LiftPosition;
import frc.team2767.command.sequence.Stow;
import frc.team2767.command.shoulder.ShoulderPosition;
import frc.team2767.subsystem.IntakeSubsystem;
import org.slf4j.Logger;

public class ScaleSecondCubeCommandGroup extends CommandGroup {

  static final Logger logger = ScaleCommandGroup.logger;

  public ScaleSecondCubeCommandGroup(ScaleSettings scaleSettings) {

    addParallel(new EnableLidar());

    addSequential(
        new CommandGroup() {
          {
            addSequential(
                new CommandGroup() {
                  {
                    addParallel(new Stow(), 1.0);
                    addParallel(
                        new PathCommand(
                            scaleSettings.getPath2(), scaleSettings.getStartPosition()));
                  }
                });

            addSequential(
                new CommandGroup() {
                  {
                    addParallel(new AzimuthCommand(scaleSettings.getAzimuth1()));
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
            addParallel(new IntakeInCubeTwo(scaleSettings.getIntakeStopDistance()), 3.0);
            addParallel(
                new DriveToCube(
                    scaleSettings.getDriveStopDistance(),
                    scaleSettings.getDrive1(),
                    scaleSettings.getStrafe1()));
          }

          @Override
          protected void end() {
            logger.trace("IntakeInCubeTwo || DriveToCube ENDED");
          }
        });

    addParallel(new DisableLidar());

    addSequential(
        new CommandGroup() {
          {
            addSequential(new StartIntakeHold());
            addParallel(new PathCommand(scaleSettings.getPath3(), scaleSettings.getAzimuth2()));
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
            addParallel(new AzimuthCommand(scaleSettings.getAzimuth3()));
            addParallel(new LiftPosition(LiftPosition.Position.SCALE_HIGH));
          }

          @Override
          protected void end() {
            logger.trace("AzimuthCommand || LiftPosition ENDED");
          }
        });

    addSequential(new ShoulderPosition(ShoulderPosition.Position.LAUNCH_SCALE));
    addSequential(new IntakeEject(IntakeSubsystem.Mode.SCALE_EJECT));
    addSequential(new Stow());
  }

  @Override
  protected void end() {
    logger.trace("ScaleSecondCubeCommandGroup ENDED");
  }
}
