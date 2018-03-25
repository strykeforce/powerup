package frc.team2767.command.auton;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.command.LogCommand;

public abstract class PowerUpCommandGroup extends CommandGroup {

  public PowerUpCommandGroup() {
    //    addSequential(
    //        new CommandGroup() {
    //          {
    //            addParallel(new ShoulderZeroWithEncoder());
    //            addParallel(new LiftZero());
    //            addParallel(new ExtenderUp());
    //            addParallel(new StartIntakeHold());
    //          }
    //        });
    addSequential(
        new LogCommand(
            "PowerUpCommandGroup default commands completed")); // TODO: remove after testing
  }
}
