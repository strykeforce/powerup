package frc.team2767.command.sequence;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.command.intake.IntakeHold;

public class HoldCube extends CommandGroup {

  public HoldCube() {
    addParallel(new IntakeHold());
    addParallel(new Stow());
  }
}
