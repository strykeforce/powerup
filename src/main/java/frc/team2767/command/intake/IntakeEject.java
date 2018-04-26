package frc.team2767.command.intake;

import edu.wpi.first.wpilibj.command.TimedCommand;
import frc.team2767.Robot;
import frc.team2767.subsystem.IntakeSubsystem;
import frc.team2767.subsystem.IntakeSubsystem.Mode;

public class IntakeEject extends TimedCommand {

  private static final double FAST_DURATION = 1.0;
  private static final double SLOW_DURATION = 2.0;

  public final Mode speed;
  private final IntakeSubsystem intakeSubsystem = Robot.INJECTOR.intakeSubsystem();

  public IntakeEject(Mode speed, double duration) {
    super(duration);
    this.speed = speed;
    requires(intakeSubsystem);
  }

  public IntakeEject(Mode mode) {
    this(mode, mode == Mode.FAST_EJECT ? FAST_DURATION : SLOW_DURATION);
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
