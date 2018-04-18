package frc.team2767.command.auton;

import com.moandjiezana.toml.Toml;
import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.Robot;
import frc.team2767.command.sequence.Stow;

public class ScaleClear extends CommandGroup {
  private final String settings;
  private static final double START_POSITION_YAW = 0d;

  ScaleClear(StartPosition startPosition) {
    settings = startPosition == StartPosition.RIGHT ? "R_SC_S_CL" : "L_SC_S_CL";
    Toml toml = Robot.INJECTOR.settings().getAutonSettings(settings);
    String path = toml.getString("path");

    PathCommand pathCommand = new PathCommand(path);
    addSequential(pathCommand);
    addSequential(new Stow(), 1.2);
  }
}
