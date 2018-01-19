package frc.team2767.command;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutonCommandGroup extends CommandGroup {
  public AutonCommandGroup() {
    addSequential(new AutonDriveCommand());
  }
}
