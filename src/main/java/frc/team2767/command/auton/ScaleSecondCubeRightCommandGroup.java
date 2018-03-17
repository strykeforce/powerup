package frc.team2767.command.auton;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.command.StartPosition;
import frc.team2767.command.drive.DriveToCube;
import frc.team2767.command.intake.IntakeInCubeTwo;
import frc.team2767.command.intake.IntakeLoad;
import frc.team2767.command.sequence.Stow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScaleSecondCubeRightCommandGroup extends CommandGroup {

  private static final Logger logger =
      LoggerFactory.getLogger(ScaleSecondCubeRightCommandGroup.class);
  private final int DRIVE_STOP_DISTANCE = 40;
  private final int INTAKE_STOP_DISTANCE = 20;

  public ScaleSecondCubeRightCommandGroup() {

    addSequential(new PathStowCommandGroup());
    addSequential(new IntakeLoad(IntakeLoad.Position.GROUND));
    addSequential(new AzimuthCommand(-45.0));

    addSequential(new DriveToSecondCube());
    //    addSequential(new IntakeHold());
    //    addSequential(new ShoulderPosition(ShoulderPosition.Position.TIGHT_STOW));
  }

  private class PathStowCommandGroup extends CommandGroup {

    public PathStowCommandGroup() {
      addParallel(new Stow());
      addSequential(new PathCommand("right_secondcube_1", StartPosition.RIGHT));
    }

    @Override
    protected void end() {
    }
  }

  private class DriveToSecondCube extends CommandGroup {

    @Override
    protected void initialize() {
    }

    public DriveToSecondCube() {
      addParallel(new IntakeInCubeTwo(INTAKE_STOP_DISTANCE));
      addSequential(new DriveToCube(DRIVE_STOP_DISTANCE, 0.1, -0.1, 0.0));
    }

    @Override
    protected void end() {
    }
  }
}
