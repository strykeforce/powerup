package frc.team2767.command.sequence;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.command.LogCommand;
import frc.team2767.command.shoulder.ShoulderPosition;

public class Stow extends CommandGroup {

  public Stow() {
    addSequential(new LogCommand("running Stow sequence"));
    addSequential(new ShoulderPosition(6250));
    //    addParallel(new LiftPosition(0));
  }
}
