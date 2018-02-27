package frc.team2767.command.intake;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.Robot;
import frc.team2767.subsystem.IntakeSubsystem;

public class IntakeOpen extends InstantCommand {

  private final IntakeSubsystem intakeSubsystem = Robot.INJECTOR.intakeSubsystem();

  public IntakeOpen() {
    super("Open");
    requires(intakeSubsystem);
  }

  @Override
  protected void initialize() {
    intakeSubsystem.open();
  }
}
