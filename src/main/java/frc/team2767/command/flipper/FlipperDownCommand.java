package frc.team2767.command.flipper;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Robot;
import frc.team2767.subsystem.FlipperSubsystem;

public class FlipperDownCommand extends Command {
  private final FlipperSubsystem flipperSubsystem;

  public FlipperDownCommand() {
    flipperSubsystem = Robot.INJECTOR.flipperSubsystem();
    requires(flipperSubsystem);
  }

  @Override
  protected void initialize() {
    flipperSubsystem.down();
  }

  @Override
  protected boolean isFinished() {
    return flipperSubsystem.isFinishedDown();
  }
}
