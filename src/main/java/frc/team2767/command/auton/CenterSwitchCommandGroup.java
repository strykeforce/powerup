package frc.team2767.command.auton;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.command.intake.IntakeEject;
import frc.team2767.command.shoulder.ShoulderZero;
import frc.team2767.subsystem.IntakeSubsystem;

public class CenterSwitchCommandGroup extends CommandGroup {

  public CenterSwitchCommandGroup(String path) {
    addParallel(new ShoulderZero());
    addSequential(new PathCommand(path));

    addSequential(new IntakeEject(IntakeSubsystem.Mode.FAST_EJECT), 20.0);
  }
}
