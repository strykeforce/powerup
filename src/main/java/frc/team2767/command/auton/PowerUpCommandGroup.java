package frc.team2767.command.auton;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.command.drive.ZeroGyroYawCommand;
import frc.team2767.command.extender.ExtenderToggle;
import frc.team2767.command.lift.LiftZero;
import frc.team2767.command.shoulder.ShoulderZero;

public abstract class PowerUpCommandGroup extends CommandGroup {

  /** Common actions for the beginning of all autons */
  public PowerUpCommandGroup() {
    addSequential(new ZeroPositionables());
  }

  static class ZeroPositionables extends CommandGroup {

    public ZeroPositionables() {
      addParallel(new ZeroGyroYawCommand());
      addParallel(new ShoulderZero());
      addParallel(new LiftZero());
      addParallel(new ExtenderToggle()); // FIXME: make a ExtenderUp command
    }
  }
}
