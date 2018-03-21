package frc.team2767.command.auton.scale;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;
import frc.team2767.command.StartPosition;
import frc.team2767.command.auton.AzimuthCommand;
import frc.team2767.command.auton.PathCommand;
import frc.team2767.command.drive.DriveToCube;
import frc.team2767.command.intake.IntakeEject;
import frc.team2767.command.intake.IntakeLoad;
import frc.team2767.command.intake.StartIntakeHold;
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
    addSequential(new DriveToSecondCube());
    addSequential(new Reverse());
    addSequential(new PrepEject());
    addSequential(new ShoulderPosition(ShoulderPosition.Position.LAUNCH_SCALE));
    addSequential(new IntakeEject(IntakeSubsystem.Mode.SCALE_EJECT));
  }

  private static class PathStowCommandGroup extends CommandGroup {
    private static final Logger logger = LoggerFactory.getLogger(PathStowCommandGroup.class);

    @Override
    protected void initialize() {
      logger.debug("init");
    }

    public PathStowCommandGroup() {
      addSequential(new StowPath());
      addSequential(new AzmIntake());
    }

    @Override
    protected void end() {
      logger.debug("end");
    }
  }

  private static class AzmIntake extends CommandGroup {

    public AzmIntake() {
      addParallel(new AzimuthCommand(-50.0));
      addSequential(new IntakeLoad(IntakeLoad.Position.GROUND), 0.25);
    }
  }

  private static class StowPath extends CommandGroup {

    public StowPath() {
      addParallel(new Stow(), 1.0);
      addSequential(new PathCommand("right_secondcube_1", StartPosition.RIGHT));
    }
  }

  private static class DriveToSecondCube extends CommandGroup {
    private static final Logger logger = LoggerFactory.getLogger(DriveToSecondCube.class);

    private final int DRIVE_STOP_DISTANCE = 55;
    private final int INTAKE_STOP_DISTANCE = 44;

    public DriveToSecondCube() {
      addParallel(new IntakeInCubeTwo(INTAKE_STOP_DISTANCE), 3.0);
      addSequential(new DriveToCube(DRIVE_STOP_DISTANCE, 0.2, -0.2, 0.0));
    }

    @Override
    protected void initialize() {
      logger.debug("init");
    }

    @Override
    protected void end() {
      logger.debug("end");
    }
  }

  private static class Reverse extends CommandGroup {
    private static final Logger logger = LoggerFactory.getLogger(DriveToSecondCube.class);

    public Reverse() {
      addSequential(new StartIntakeHold());
      addParallel(new PathCommand("right_secondcube_backtoscale", -130.0));
      addSequential(new WaitCommand(0.5));
      addSequential(new Stow());
    }

    @Override
    protected void initialize() {
      logger.debug("init");
    }

    @Override
    protected void end() {
      logger.debug("end");
    }
  }

  private static class PrepEject extends CommandGroup {

    public PrepEject() {
      addParallel(new AzimuthCommand(20.0));
      addSequential(new LiftPosition(LiftPosition.Position.SCALE_HIGH));
    }
  }
}
