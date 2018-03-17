package frc.team2767.command.sequence;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.command.lift.LiftPosition;
import frc.team2767.command.shoulder.ShoulderPosition;

public class Stow extends CommandGroup {

  public Stow() {
    addParallel(new LiftPosition(LiftPosition.Position.STOW));
    addParallel(new ShoulderPosition(ShoulderPosition.Position.STOW));
  }

  @Override
  protected void end() {
  }
}
