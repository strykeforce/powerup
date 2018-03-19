package frc.team2767.command.test;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.command.auton.AzimuthCommand;

public class AzimuthTestCommandGroup extends CommandGroup {

  public AzimuthTestCommandGroup() {
    addSequential(new AzimuthCommand(-45.0));
  }
}
