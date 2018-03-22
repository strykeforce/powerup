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

// FIXME: this probably can be generalized like ScaleCommandGroup
public class ScaleSecondCubeRightCommandGroup extends CommandGroup {

  static final Logger logger = ScaleCommandGroup.logger;

  private final int DRIVE_STOP_DISTANCE = 44;
  private final int INTAKE_STOP_DISTANCE = 35;

  public ScaleSecondCubeRightCommandGroup(ScaleSettings scaleSettings) {

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
                            scaleSettings.getkPath2(), scaleSettings.getkStartPosition()));
                  }
                });

            addSequential(
                new CommandGroup() {
                  {
                    addParallel(new AzimuthCommand(scaleSettings.getkAzimuth1()));
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
            addParallel(new IntakeInCubeTwo(scaleSettings.getkIntakeStopDistance()), 3.0);
            addParallel(
                new DriveToCube(
                    scaleSettings.getkDriveStopDistance(),
                    scaleSettings.getkDrive1(),
                    scaleSettings.getkStrafe1()));
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
            addParallel(new PathCommand(scaleSettings.getkPath3(), scaleSettings.getkAzimuth2()));
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
            addParallel(new AzimuthCommand(scaleSettings.getkAzimuth3()));
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
