package frc.team2767.command.auton.scale;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.command.StartPosition;
import frc.team2767.command.auton.AzimuthCommand;
import frc.team2767.command.auton.PathCommand;
import frc.team2767.command.drive.DriveToCube;
import frc.team2767.command.intake.IntakeEject;
import frc.team2767.command.intake.IntakeHold;
import frc.team2767.command.intake.IntakeLoad;
import frc.team2767.command.lift.LiftPosition;
import frc.team2767.command.sequence.Stow;
import frc.team2767.command.shoulder.ShoulderPosition;
import frc.team2767.subsystem.IntakeSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// FIXME: this probably can be generalized like ScaleCommandGroup
public class ScaleSecondCubeRightCommandGroup extends CommandGroup {

  private static final Logger logger =
      LoggerFactory.getLogger(ScaleSecondCubeRightCommandGroup.class);

  public ScaleSecondCubeRightCommandGroup() {

    addSequential(new PathStowCommandGroup());
    addSequential(new IntakeLoad(IntakeLoad.Position.GROUND));
    addSequential(new AzimuthCommand(-45.0));

    addSequential(new DriveToSecondCube());
    addSequential(new IntakeHold());
    addSequential(new BackToScale());
    addSequential(new AzimuthCommand(45.0));
    addSequential(new LiftPosition(LiftPosition.Position.SCALE_HIGH));
    addSequential(new IntakeEject(IntakeSubsystem.Mode.SCALE_EJECT));
  }

  private static class PathStowCommandGroup extends CommandGroup {

    public PathStowCommandGroup() {
      addParallel(new Stow());
      addSequential(new PathCommand("right_secondcube_1", StartPosition.RIGHT));
    }
  }

  private static class DriveToSecondCube extends CommandGroup {

    private final int driveStopDistance = 40;
    private final int intakeStopDistance = 20;

    public DriveToSecondCube() {
      addParallel(new IntakeInCubeTwo(intakeStopDistance));
      addSequential(new DriveToCube(driveStopDistance, 0.1, -0.1, 0.0));
    }
  }

  private static class BackToScale extends CommandGroup {

    public BackToScale() {
      addParallel(new PathCommand("right_secondcube_backtoscale", StartPosition.RIGHT));
      addSequential(new ShoulderPosition(ShoulderPosition.Position.TIGHT_STOW));
    }
  }
}
