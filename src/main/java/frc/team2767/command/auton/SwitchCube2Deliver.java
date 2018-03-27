package frc.team2767.command.auton;

import static frc.team2767.command.auton.PowerUpGameFeature.SCALE;

import com.moandjiezana.toml.Toml;
import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.Robot;
import java.util.HashMap;
import java.util.Map;
import openrio.powerup.MatchData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SwitchCube2Deliver extends InstantCommand {

  private static final Logger logger = LoggerFactory.getLogger(SwitchCube2Deliver.class);

  private static final Map<Scenario, String> SETTINGS = new HashMap<>();

  static {
    //    SETTINGS.put(new Scenario(StartPosition.LEFT, SCALE, MatchData.OwnedSide.LEFT),
    // "L_SW_S_C2D");
    //    SETTINGS.put(new Scenario(StartPosition.LEFT, SCALE, MatchData.OwnedSide.RIGHT),
    // "L_SW_O_C2D");
    //    SETTINGS.put(new Scenario(StartPosition.RIGHT, SCALE, MatchData.OwnedSide.LEFT),
    // "R_SW_O_C2D");
    //    SETTINGS.put(new Scenario(StartPosition.RIGHT, SCALE, MatchData.OwnedSide.RIGHT),
    // "R_SW_S_C2D");
  }

  //  private final double kLeftEjectAzimuth;
  //  private final double kRightEjectAzimuth;

  //  private final Command leftPath;
  //  private final Command rightPath;

  private String settings;

  public SwitchCube2Deliver(StartPosition startPosition) {
    String settings = SETTINGS.get(new Scenario(startPosition, SCALE, MatchData.OwnedSide.LEFT));
    Toml toml = Robot.INJECTOR.settings().getAutonSettings(settings);
    //    leftPath = new PathCommand(toml.getString("path"));

    //    kLeftEjectAzimuth = toml.getDouble("ejectAzimuth");

    settings = SETTINGS.get(new Scenario(startPosition, SCALE, MatchData.OwnedSide.RIGHT));
    toml = Robot.INJECTOR.settings().getAutonSettings(settings);
    //    rightPath = new PathCommand(toml.getString("path"));

    //    kRightEjectAzimuth = toml.getDouble("ejectAzimuth");
  }

  @Override
  public String toString() {
    return "SwitchCube2Deliver{" + "settings='" + settings + '\'' + '}';
  }
}
