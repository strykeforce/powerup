package frc.team2767.command.intake;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.Robot;
import frc.team2767.subsystem.IntakeSubsystem;
import frc.team2767.subsystem.IntakeSubsystem.Mode;

public class IntakeIn extends InstantCommand {

  private final IntakeSubsystem intakeSubsystem = Robot.INJECTOR.intakeSubsystem();

  public IntakeIn() {
    super("Intake In");
    requires(intakeSubsystem);
  }

  @Override
  protected void initialize() {
    intakeSubsystem.run(Mode.LOAD);
  }
}
