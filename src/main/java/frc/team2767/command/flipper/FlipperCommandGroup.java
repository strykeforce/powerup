package frc.team2767.command.flipper;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class FlipperCommandGroup extends CommandGroup {
  public FlipperCommandGroup() {
    addSequential(new FlipperUpCommand());
    addSequential(new FlipperStopCommand());
    addSequential(new FlipperDownCommand());
  }
}
