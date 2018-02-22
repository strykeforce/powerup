package frc.team2767.command.auton;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.command.shoulder.ShoulderZero;

public abstract class PowerUpCommandGroup extends CommandGroup {

  /** Common actions for the beginning of all autons */
  public PowerUpCommandGroup() {
    addParallel(new ShoulderZero());
  }
}
