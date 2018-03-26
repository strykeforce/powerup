package frc.team2767.command.auton;

import com.moandjiezana.toml.Toml;
import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.Robot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SwitchSameCube1Deliver extends CommandGroup {

  private final int kDistance;
  private final String settings;

  private static final Logger logger = LoggerFactory.getLogger(SwitchSameCube1Deliver.class);

  public SwitchSameCube1Deliver(StartPosition startPosition) {
    settings = startPosition == StartPosition.RIGHT ? "R_SW_S_C1D" : "L_SW_S_C1D";
    Toml toml = Robot.INJECTOR.settings().getAutonSettings(settings);
    String path = toml.getString("path");
    kDistance = toml.getLong("distance").intValue();

    PathCommand pathCommand = new PathCommand(path, startPosition);
  }
}
