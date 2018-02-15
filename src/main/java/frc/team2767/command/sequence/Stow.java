package frc.team2767.command.sequence;

import com.moandjiezana.toml.Toml;
import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.Robot;
import frc.team2767.Settings;
import frc.team2767.command.LogCommand;
import frc.team2767.command.lift.LiftPosition;
import frc.team2767.command.shoulder.ShoulderPosition;

public class Stow extends CommandGroup {

  public Stow() {
    Settings settings = Robot.INJECTOR.settings();
    Toml toml = settings.getTable("POWERUP.SHOULDER");
    int shoulderStow = toml.getLong("stowPosition").intValue();
    toml = settings.getTable("POWERUP.LIFT");
    int liftStow = toml.getLong("stowPosition").intValue();

    addParallel(
        new LogCommand(
            "Stow sequence setting shoulder = " + shoulderStow + ", lift = " + liftStow));
    addParallel(new LiftPosition(liftStow));
    addParallel(new ShoulderPosition(shoulderStow));
  }
}
