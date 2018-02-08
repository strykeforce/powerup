package frc.team2767.command.flipper;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.Robot;
import frc.team2767.subsystem.FlipperSubsystem;

public class FlipperStopCommand extends InstantCommand {
  private final FlipperSubsystem flipperSubsystem;

  public FlipperStopCommand() {
    flipperSubsystem = Robot.INJECTOR.flipperSubsystem();
    requires(flipperSubsystem);
  }

  @Override
  protected void initialize() {
    flipperSubsystem.stop();
  }
}
