package frc.team2767.command.auton;

import com.moandjiezana.toml.Toml;
import frc.team2767.Robot;
import frc.team2767.Settings;
import frc.team2767.command.intake.IntakeEject;
import frc.team2767.command.sequence.Stow;
import frc.team2767.command.shoulder.ShoulderPosition;
import frc.team2767.subsystem.IntakeSubsystem;

public class SwitchCommandGroup extends PowerUpCommandGroup {

  public SwitchCommandGroup(Side side) {
    super();
    Settings settings = Robot.INJECTOR.settings();
    Toml toml = settings.getTable("POWERUP.SHOULDER");
    addParallel(
        new ShoulderPosition(
            toml.getLong("stowPosition").intValue())); // TODO: make std positions enum

    addSequential(new PathCommand(side.path));
    addSequential(new AzimuthCommand(side.azimuth));

    addSequential(new IntakeEject(IntakeSubsystem.Mode.FAST_EJECT));
    addSequential(new Stow());
  }

  public enum Side {
    LEFT("left_switch", 90.0),
    RIGHT("right_switch", -90.0),
    ;

    private final String path;
    private final double azimuth;

    Side(String path, double azimuth) {
      this.path = path;
      this.azimuth = azimuth;
    }
  }
}
