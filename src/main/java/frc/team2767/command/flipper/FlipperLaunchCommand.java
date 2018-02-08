package frc.team2767.command.flipper;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class FlipperLaunchCommand extends CommandGroup {
  public FlipperLaunchCommand() {
    addSequential(new FlipperUpCommand());
    addSequential(new FlipperStopCommand());
    addSequential(new FlipperDownCommand());
  }
}
