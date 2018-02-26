package frc.team2767.command.auton;

import com.moandjiezana.toml.Toml;
import frc.team2767.Robot;
import frc.team2767.Settings;
import frc.team2767.command.intake.IntakeEject;
import frc.team2767.command.lift.LiftPosition;
import frc.team2767.command.shoulder.ShoulderPosition;
import frc.team2767.subsystem.IntakeSubsystem;

public class LeftScaleCommandGroup extends PowerUpCommandGroup {

  public LeftScaleCommandGroup(String path) {
    super();
    Settings settings = Robot.INJECTOR.settings();
    Toml toml = settings.getTable("POWERUP.SHOULDER");
    addParallel(new ShoulderPosition(toml.getLong("stowPosition").intValue()));
    toml = settings.getTable("POWERUP.LIFT");
    addParallel(new LiftPosition(toml.getLong("scaleHighPosition").intValue()));
    addSequential(new PathCommand(path));

    addSequential(new IntakeEject(IntakeSubsystem.Mode.SLOW_EJECT));
  }
}
