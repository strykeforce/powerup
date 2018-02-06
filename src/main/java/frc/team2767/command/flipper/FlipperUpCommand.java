package frc.team2767.command.flipper;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Robot;
import frc.team2767.subsystem.FlipperSubsystem;

public class FlipperUpCommand extends Command {

  private final FlipperSubsystem flipperSubsystem;

  public FlipperUpCommand() {
    flipperSubsystem = Robot.INJECTOR.flipSubsystem();
    requires(flipperSubsystem);
  }

  @Override
  protected void initialize() {
    flipperSubsystem.run();
  }

  @Override
  protected boolean isFinished() {
    return flipperSubsystem.isFinishedUp();
  }
}
