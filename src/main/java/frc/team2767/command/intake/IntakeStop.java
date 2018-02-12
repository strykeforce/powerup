package frc.team2767.command.intake;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.Robot;
import frc.team2767.subsystem.IntakeSubsystem;

public class IntakeStop extends InstantCommand {

  private final IntakeSubsystem intakeSubsystem = Robot.INJECTOR.intakeSubsystem();

  public IntakeStop() {
    requires(intakeSubsystem);
  }

  @Override
  protected void initialize() {
    intakeSubsystem.stop();
  }
}
