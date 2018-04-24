package frc.team2767.command.auton;

import static frc.team2767.command.auton.PowerUpGameFeature.SCALE;
import static frc.team2767.command.auton.PowerUpGameFeature.SWITCH;

import com.moandjiezana.toml.Toml;
import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.Robot;
import frc.team2767.command.shoulder.ShoulderPosition;
import java.util.HashMap;
import java.util.Map;
import openrio.powerup.MatchData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SwitchCube3Deliver extends CommandGroup implements OwnedSidesSettable {
  private static final Logger logger = LoggerFactory.getLogger(SwitchCube2Deliver.class);

  private static final Map<Scenario, String> SETTINGS = new HashMap<>();

  static {
    SETTINGS.put(new Scenario(StartPosition.LEFT, SCALE, MatchData.OwnedSide.LEFT), "L_SW_S_C3D");
    SETTINGS.put(new Scenario(StartPosition.LEFT, SCALE, MatchData.OwnedSide.RIGHT), "L_SW_O_C3D");
    SETTINGS.put(new Scenario(StartPosition.RIGHT, SCALE, MatchData.OwnedSide.LEFT), "R_SW_O_C3D");
    SETTINGS.put(new Scenario(StartPosition.RIGHT, SCALE, MatchData.OwnedSide.RIGHT), "R_SW_S_C3D");
    SETTINGS.put(new Scenario(StartPosition.LEFT, SWITCH, MatchData.OwnedSide.LEFT), "L_SW_S_C3D");
    SETTINGS.put(new Scenario(StartPosition.LEFT, SWITCH, MatchData.OwnedSide.RIGHT), "L_SW_O_C3D");
    SETTINGS.put(
        new Scenario(StartPosition.RIGHT, SWITCH, MatchData.OwnedSide.RIGHT), "R_SW_S_C3D");
    SETTINGS.put(new Scenario(StartPosition.RIGHT, SWITCH, MatchData.OwnedSide.LEFT), "R_SW_O_C3D");
  }

  private final double kLeftDirection;
  private final int kLeftDistance;
  private final double kLeftAzimuth;
  private final double kRightDirection;
  private final int kRightDistance;
  private final double kRightAzimuth;

  private String settings;

  SwitchCube3Deliver(StartPosition startPosition) {
    String settings = SETTINGS.get(new Scenario(startPosition, SCALE, MatchData.OwnedSide.LEFT));
    Toml toml = Robot.INJECTOR.settings().getAutonSettings(settings);
    kLeftDirection = toml.getDouble("direction");
    kLeftDistance = toml.getLong("distance").intValue();
    kLeftAzimuth = toml.getDouble("azimuth");

    settings = SETTINGS.get(new Scenario(startPosition, SCALE, MatchData.OwnedSide.RIGHT));
    toml = Robot.INJECTOR.settings().getAutonSettings(settings);
    kRightDirection = toml.getDouble("direction");
    kRightDistance = toml.getLong("distance").intValue();
    kRightAzimuth = toml.getDouble("azimuth");
  }

  @Override
  public void setOwnedSide(
      StartPosition startPosition, MatchData.OwnedSide nearSwitch, MatchData.OwnedSide scale) {
    boolean isLeft = nearSwitch == MatchData.OwnedSide.LEFT;
    settings = SETTINGS.get(new Scenario(startPosition, SCALE, scale));

    logger.debug("start position = {}", startPosition);
    logger.debug("settings = {}", settings);
    logger.debug("LDirec = {}, LDist = {}, LAzi = {}", kLeftDirection, kLeftDistance, kLeftAzimuth);
    logger.debug(
        "RDirec = {}, RDist = {}, RAzi = {}", kRightDirection, kRightDistance, kRightAzimuth);

    addParallel(
        isLeft
            ? new MotionDrive(kLeftDirection, kLeftDistance, kLeftAzimuth)
            : new MotionDrive(kRightDirection, kRightDistance, kRightAzimuth));
    addSequential(new ShoulderPosition(ShoulderPosition.Position.STOW));
    //    addSequential(new IntakeEject(IntakeSubsystem.Mode.SWITCH_EJECT));
  }

  @Override
  public String toString() {
    return "SwitchCube3Deliver{" + "settings='" + settings + '\'' + '}';
  }
}
