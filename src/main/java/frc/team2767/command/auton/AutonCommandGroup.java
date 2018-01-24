package frc.team2767.command.auton;

import edu.wpi.first.wpilibj.command.CommandGroup;

/** Command group for autonomous mode. */
public class AutonCommandGroup extends CommandGroup {
  public AutonCommandGroup() {
    addSequential(new CrabToSwitchAutonCommand(0.755, -0.656, 0.01, 86));
    addSequential(new CrabToSwitchAutonCommand(1, 0, 0, 2));
  }
}
