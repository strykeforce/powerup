package frc.team2767.command.auton;

import frc.team2767.command.intake.IntakeEject;
import frc.team2767.command.shoulder.ShoulderPosition;
import frc.team2767.subsystem.IntakeSubsystem;

public class CenterSwitchCommandGroup extends PowerUpCommandGroup {

  public CenterSwitchCommandGroup(String path) {
    super();
    addParallel(new ShoulderPosition(6500));
    addSequential(new PathCommand(path));
    addSequential(new IntakeEject(IntakeSubsystem.Mode.FAST_EJECT));
  }
}
