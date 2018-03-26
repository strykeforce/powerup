package frc.team2767.command.auton;

import com.moandjiezana.toml.Toml;
import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.Robot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SwitchOppositeCube1Deliver extends InstantCommand {
  private static final Logger logger = LoggerFactory.getLogger(SwitchOppositeCube1Deliver.class);
  private final String settings;

  public SwitchOppositeCube1Deliver(StartPosition startPosition) {
    settings = startPosition == StartPosition.RIGHT ? "R_SW_O_C1D" : "L_SW_O_C1D";
    Toml toml = Robot.INJECTOR.settings().getAutonSettings(settings);
    String kPath = toml.getString("path");
    double kAzimuth = toml.getDouble("azimuth");
  }

  @Override
  public String toString() {
    return "SwitchOppositeCube1Deliver{" + "settings='" + settings + '\'' + '}';
  }
}
