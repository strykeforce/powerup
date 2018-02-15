package frc.team2767.command.intake;

import static frc.team2767.subsystem.IntakeSubsystem.Mode.FAST_EJECT;

import edu.wpi.first.wpilibj.command.TimedCommand;
import frc.team2767.Robot;
import frc.team2767.subsystem.IntakeSubsystem;

public class IntakeEject extends TimedCommand {

  private final IntakeSubsystem intakeSubsystem = Robot.INJECTOR.intakeSubsystem();

  public IntakeEject() {
    super(0.5);
  }

  @Override
  protected void initialize() {
    intakeSubsystem.run(FAST_EJECT);
  }

  @Override
  protected void end() {
    intakeSubsystem.stop();
  }
}
