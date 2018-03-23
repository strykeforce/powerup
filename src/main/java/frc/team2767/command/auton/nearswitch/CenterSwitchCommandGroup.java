package frc.team2767.command.auton.nearswitch;

import frc.team2767.command.auton.PathCommand;
import frc.team2767.command.auton.PowerUpCommandGroup;
import frc.team2767.command.intake.IntakeEject;
import frc.team2767.command.shoulder.ShoulderPosition;
import frc.team2767.subsystem.IntakeSubsystem;

public class CenterSwitchCommandGroup extends PowerUpCommandGroup {

  public CenterSwitchCommandGroup(String path) {
    addParallel(new ShoulderPosition(ShoulderPosition.Position.LAUNCH_SWITCH));
    addSequential(new PathCommand(path));
    addSequential(new IntakeEject(IntakeSubsystem.Mode.FAST_EJECT));
  }
}
