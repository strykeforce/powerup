package frc.team2767.command.intake;

import static frc.team2767.subsystem.IntakeSubsystem.Mode.HOLD;
import static frc.team2767.subsystem.IntakeSubsystem.Mode.LOAD;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.TimedCommand;
import frc.team2767.Robot;
import frc.team2767.subsystem.IntakeSubsystem;

public class IntakeLoad extends CommandGroup {

  public IntakeLoad() {
    addSequential(new Intake());
    addSequential(new Hold());
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
      return intakeSubsystem.isLoaded();
    }
  }

  static class Hold extends TimedCommand {

    private final IntakeSubsystem intakeSubsystem;

    public Hold() {
      super(0.75);
      intakeSubsystem = Robot.INJECTOR.intakeSubsystem();
      requires(intakeSubsystem);
    }

    @Override
    protected void initialize() {
      intakeSubsystem.run(HOLD);
    }

    @Override
    protected boolean isFinished() {
      return isTimedOut() && intakeSubsystem.isLoaded();
    }

    @Override
    protected void end() {
      intakeSubsystem.stop();
    }
  }
}
