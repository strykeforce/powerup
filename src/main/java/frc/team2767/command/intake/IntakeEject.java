package frc.team2767.command.intake;

import edu.wpi.first.wpilibj.command.TimedCommand;
import frc.team2767.Robot;
import frc.team2767.subsystem.IntakeSubsystem;
import frc.team2767.subsystem.IntakeSubsystem.Mode;

public class IntakeEject extends TimedCommand {

  private final IntakeSubsystem intakeSubsystem = Robot.INJECTOR.intakeSubsystem();
  public final Mode speed;

  public IntakeEject(Mode mode) {
    super(mode == Mode.FAST_EJECT ? 0.5 : 0.5); // FIXME: constants for timeout
    speed = mode;
    requires(intakeSubsystem);
  }

  @Override
  protected void initialize() {
    intakeSubsystem.run(speed);
  }

  @Override
  protected void end() {
    intakeSubsystem.stop();
  }
}
