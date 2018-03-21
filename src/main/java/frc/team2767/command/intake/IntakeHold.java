package frc.team2767.command.intake;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Robot;
import frc.team2767.subsystem.IntakeSubsystem;

public class IntakeHold extends Command {

  private final IntakeSubsystem intakeSubsystem = Robot.INJECTOR.intakeSubsystem();

  public IntakeHold() {
    requires(intakeSubsystem);
  }

  @Override
  protected void initialize() {
    System.out.println("Holding");
    intakeSubsystem.run(IntakeSubsystem.Mode.HOLD);
  }

  @Override
  protected boolean isFinished() {
    return false;
  }

  @Override
  protected void end() {
    intakeSubsystem.stop();
  }
}
