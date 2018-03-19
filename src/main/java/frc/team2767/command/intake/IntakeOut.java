package frc.team2767.command.intake;

import static frc.team2767.subsystem.IntakeSubsystem.Mode.SLOW_EJECT;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.Robot;
import frc.team2767.subsystem.IntakeSubsystem;

public class IntakeOut extends InstantCommand {

  private final IntakeSubsystem intakeSubsystem = Robot.INJECTOR.intakeSubsystem();

  public IntakeOut() {
    super("Intake Out");
    requires(intakeSubsystem);
  }

  @Override
  protected void initialize() {
    intakeSubsystem.run(SLOW_EJECT);
  }
}
