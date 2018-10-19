package frc.team2767.command.shoulder;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.command.intake.IntakeLoad;
import frc.team2767.command.intake.IntakeOpen;

public class ShoulderDownIntakeOpen extends CommandGroup {
  public ShoulderDownIntakeOpen() {
    addParallel(new IntakeLoad(IntakeLoad.Position.GROUND));
    addSequential(new IntakeOpen());
  }
}
