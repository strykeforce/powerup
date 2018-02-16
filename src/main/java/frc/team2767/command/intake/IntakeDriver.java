package frc.team2767.command.intake;

import static frc.team2767.subsystem.IntakeSubsystem.Mode.LOAD;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.TimedCommand;
import frc.team2767.Robot;
import frc.team2767.subsystem.IntakeSubsystem;
import frc.team2767.subsystem.LiftSubsystem;
import frc.team2767.subsystem.ShoulderSubsystem;

public class IntakeDriver extends CommandGroup {

  public IntakeDriver() {
    addSequential(new DropLift());
    addSequential(new DropShoulder());
    addSequential(new Intake());
    addSequential(new Hold());
    addSequential(new StowShoulder());
  }

  // lower lift to lowest position
  static class DropLift extends Command {

    private final LiftSubsystem liftSubsystem = Robot.INJECTOR.liftSubsystem();

    public DropLift() {
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

    public DropShoulder() {
      requires(shoulderSubsystem);
    }

    @Override
    protected void initialize() {
      shoulderSubsystem.setPosition(0);
    }

    @Override
    protected boolean isFinished() {
      return shoulderSubsystem.onTarget();
    }
  }

  // run the intake until a block triggers the limit switch
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
      // intakeSubsystem.isLoaded();
      // FIXME convert from a delay to a stop when limit switch is pressed. Limit switches are
      // currently broken.
    }
    /*
    @Override
    protected void end() {
      intakeSubsystem.stop();
    }*/
  }

  // continue to run the intake to ensure the block is grasped in the correct orientation
  static class Hold extends TimedCommand {

    private final IntakeSubsystem intakeSubsystem;

    public Hold() {
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
      return isTimedOut() /*&& intakeSubsystem.isLoaded()*/;
    }

    @Override
    protected void end() {
      intakeSubsystem.stop();
    }
  }

  // lift the shoulder so the lift can be raised with the block
  static class StowShoulder extends Command {

    private final ShoulderSubsystem shoulderSubsystem = Robot.INJECTOR.shoulderSubsystem();

    public StowShoulder() {
      requires(shoulderSubsystem);
    }

    @Override
    protected void initialize() {
      shoulderSubsystem.setPosition(6250);
    }

    @Override
    protected boolean isFinished() {
      return shoulderSubsystem.onTarget();
    }

    /*@Override
    protected void end() {
      shoulderSubsystem.stop();
    }*/
  }
}
