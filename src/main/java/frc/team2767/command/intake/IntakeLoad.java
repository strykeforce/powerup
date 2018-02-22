package frc.team2767.command.intake;

import static frc.team2767.subsystem.IntakeSubsystem.Mode.LOAD;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.Robot;
import frc.team2767.subsystem.IntakeSubsystem;
import frc.team2767.subsystem.LiftSubsystem;
import frc.team2767.subsystem.ShoulderSubsystem;

public class IntakeLoad extends CommandGroup {

  public IntakeLoad() {
    addSequential(new Intake());
    addSequential(new DropLift());
    addSequential(new DropShoulder());
    // addSequential(new Hold());
    //    addSequential(new StowShoulder());
  }

  static class Intake extends Command {

    private final IntakeSubsystem intakeSubsystem = Robot.INJECTOR.intakeSubsystem();

    public Intake() {
      requires(intakeSubsystem);
    }

    @Override
    protected void initialize() {
      intakeSubsystem.run(LOAD);
    }

    @Override
    protected boolean isFinished() {
      return true;
    }
  }

  // lower lift to lowest position
  static class DropLift extends Command {

    private final LiftSubsystem liftSubsystem = Robot.INJECTOR.liftSubsystem();

    DropLift() {
      requires(liftSubsystem);
    }

    @Override
    protected void initialize() {
      liftSubsystem.setPosition(0);
    }

    @Override
    protected boolean isFinished() {
      return liftSubsystem.onTarget();
    }
  }

  // lower shoulder to position used when loading cube
  static class DropShoulder extends Command {

    private final ShoulderSubsystem shoulderSubsystem = Robot.INJECTOR.shoulderSubsystem();
    private final int position;

    DropShoulder() {
      position =
          Robot.INJECTOR
              .settings()
              .getTable("POWERUP.SHOULDER")
              .getLong("intakePosition")
              .intValue();
      requires(shoulderSubsystem);
    }

    @Override
    protected void initialize() {
      shoulderSubsystem.setPosition(position);
    }

    @Override
    protected boolean isFinished() {
      return true;
      //return shoulderSubsystem.onTarget();
    }
  }

  // run the intake until a block triggers the limit switch


  // continue to run the intake to ensure the block is grasped in the correct orientation
  /*static class Hold extends TimedCommand {

    private final IntakeSubsystem intakeSubsystem;

    Hold() {
      super(2);
      intakeSubsystem = Robot.INJECTOR.intakeSubsystem();
      requires(intakeSubsystem);
    }

    @Override
    protected void initialize() {
      intakeSubsystem.run(LOAD);
    }

    @Override
    protected boolean isFinished() {
      return isTimedOut() /*&& intakeSubsystem.isLoaded();
    }

    @Override
    protected void end() {
      intakeSubsystem.stop();
    }
  }*/


}
