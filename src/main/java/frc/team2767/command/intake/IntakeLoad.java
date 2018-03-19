package frc.team2767.command.intake;

import static frc.team2767.subsystem.IntakeSubsystem.Mode.LOAD;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.Robot;
import frc.team2767.command.lift.LiftPosition;
import frc.team2767.command.shoulder.ShoulderPosition;
import frc.team2767.subsystem.IntakeSubsystem;

public class IntakeLoad extends CommandGroup {

  @Override
  protected void initialize() {}

  @Override
  protected void end() {}

  public IntakeLoad(Position position) {
    addParallel(new Intake());
    LiftPosition.Position liftPosition;
    ShoulderPosition.Position shoulderPosition;

    if (position == Position.GROUND) {
      liftPosition = LiftPosition.Position.STOW;
      shoulderPosition = ShoulderPosition.Position.INTAKE;
    } else {
      liftPosition = LiftPosition.Position.PORTAL;
      shoulderPosition = ShoulderPosition.Position.INTAKE;
    }

    addParallel(new LiftPosition(liftPosition));
    addSequential(new ShoulderPosition(shoulderPosition));
  }

  public enum Position {
    PORTAL,
    GROUND
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
}
