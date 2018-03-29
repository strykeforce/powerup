package frc.team2767.command.auton;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.command.extender.ExtenderUp;
import frc.team2767.command.intake.StartIntakeHold;
import frc.team2767.command.lift.LiftZero;
import frc.team2767.command.shoulder.ShoulderZeroWithEncoder;

public abstract class PowerUpCommandGroup extends CommandGroup {

  public PowerUpCommandGroup() {
    addSequential(
        new CommandGroup() {
          {
            addParallel(new ShoulderZeroWithEncoder());
            addParallel(new LiftZero());
            addParallel(new ExtenderUp());
            addParallel(new StartIntakeHold());
          }
        });
  }
}
