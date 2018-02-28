package frc.team2767.command.auton;

import com.moandjiezana.toml.Toml;
import frc.team2767.Robot;
import frc.team2767.Settings;
import frc.team2767.command.intake.IntakeEject;
import frc.team2767.command.sequence.Stow;
import frc.team2767.command.shoulder.ShoulderPosition;
import frc.team2767.subsystem.IntakeSubsystem;

public class RightSwitchCommandGroup extends PowerUpCommandGroup {
  public RightSwitchCommandGroup(String path) {
    super();
    Settings settings = Robot.INJECTOR.settings();
    Toml toml = settings.getTable("POWERUP.SHOULDER");
    addParallel(new ShoulderPosition(toml.getLong("stowPosition").intValue())); // FIXME: use enum

    addSequential(new PathCommand(path));

    addSequential(new AzimuthCommand(-90));

    addSequential(new IntakeEject(IntakeSubsystem.Mode.FAST_EJECT));
    addSequential(new Stow());
  }
}
