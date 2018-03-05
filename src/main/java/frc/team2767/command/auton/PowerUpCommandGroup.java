package frc.team2767.command.auton;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.command.extender.ExtenderUp;
import frc.team2767.command.lift.LiftZero;
import frc.team2767.command.shoulder.ShoulderZeroWithLimitSwitch;

public abstract class PowerUpCommandGroup extends CommandGroup {

  /** Common actions for the beginning of all autons */
  public PowerUpCommandGroup() {
    addSequential(new ZeroPositionables());
  }

  static class ZeroPositionables extends CommandGroup {

    public ZeroPositionables() {
      addParallel(new ShoulderZeroWithLimitSwitch());
      addParallel(new LiftZero());
      addParallel(new ExtenderUp());
    }
  }
}
