package frc.team2767.command.auton;

import static frc.team2767.command.auton.PowerUpGameFeature.SCALE;

import com.moandjiezana.toml.Toml;
import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.Robot;
import frc.team2767.command.intake.IntakeEject;
import frc.team2767.command.shoulder.ShoulderPosition;
import frc.team2767.subsystem.IntakeSubsystem;
import java.util.HashMap;
import java.util.Map;
import openrio.powerup.MatchData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SwitchCube2Deliver extends CommandGroup implements OwnedSidesSettable {

  private static final Logger logger = LoggerFactory.getLogger(SwitchCube2Deliver.class);

  private static final Map<Scenario, String> SETTINGS = new HashMap<>();

  static {
    SETTINGS.put(new Scenario(StartPosition.LEFT, SCALE, MatchData.OwnedSide.LEFT), "L_SW_S_C2D");
    SETTINGS.put(new Scenario(StartPosition.LEFT, SCALE, MatchData.OwnedSide.RIGHT), "L_SW_O_C2D");
    SETTINGS.put(new Scenario(StartPosition.RIGHT, SCALE, MatchData.OwnedSide.LEFT), "R_SW_O_C2D");
    SETTINGS.put(new Scenario(StartPosition.RIGHT, SCALE, MatchData.OwnedSide.RIGHT), "R_SW_S_C2D");
  }

  private final double kEjectLeftAzimuth;
  private final double kEjectRightAzimuth;
  private final double kRightDrive1;
  private final double kRightDrive2;
  private final double kRightStrafe1;
  private final double kRightStrafe2;

  private final double kLeftDrive1;
  private final double kLeftDrive2;
  private final double kLeftStrafe1;
  private final double kLeftStrafe2;

  private String settings;

  public SwitchCube2Deliver(StartPosition startPosition) {
    String settings = SETTINGS.get(new Scenario(startPosition, SCALE, MatchData.OwnedSide.LEFT));
    Toml toml = Robot.INJECTOR.settings().getAutonSettings(settings);

    kEjectLeftAzimuth = toml.getDouble("ejectAzimuth");
    kLeftDrive1 = toml.getDouble("drive1");
    kLeftDrive2 = toml.getDouble("drive2");
    kLeftStrafe1 = toml.getDouble("strafe1");
    kLeftStrafe2 = toml.getDouble("strafe2");

    settings = SETTINGS.get(new Scenario(startPosition, SCALE, MatchData.OwnedSide.RIGHT));
    toml = Robot.INJECTOR.settings().getAutonSettings(settings);

    kEjectRightAzimuth = toml.getDouble("ejectAzimuth");
    kRightDrive1 = toml.getDouble("drive1");
    kRightDrive2 = toml.getDouble("drive2");
    kRightStrafe1 = toml.getDouble("strafe1");
    kRightStrafe2 = toml.getDouble("strafe2");
  }

  @Override
  public void setOwnedSide(
      StartPosition startPosition, MatchData.OwnedSide nearSwitch, MatchData.OwnedSide scale) {
    boolean isLeft = nearSwitch == MatchData.OwnedSide.LEFT;
    settings = SETTINGS.get(new Scenario(startPosition, SCALE, scale));

    logger.debug("start position = {}", startPosition);
    logger.debug("settings = {}", settings);
    logger.debug(
        "LAzm = {}, LDr1 = {}, LDr2 = {}, LStrf1 = {}, LStrf2  ={}",
        kEjectLeftAzimuth,
        kLeftDrive1,
        kLeftDrive2,
        kLeftStrafe1,
        kLeftStrafe2);
    logger.debug(
        "RAzm = {}, RDr1 = {}, RDr2 = {}, RStrf1 = {}, RStrf2  ={}",
        kEjectRightAzimuth,
        kRightDrive1,
        kLeftDrive2,
        kRightStrafe1,
        kRightStrafe2);

    addSequential(
        new CommandGroup() {
          {
            addParallel(
                new TimedDrive(
                    0.5,
                    isLeft ? kLeftDrive1 : kRightDrive1,
                    isLeft ? kLeftStrafe1 : kRightStrafe1,
                    0.0));
            addSequential(new ShoulderPosition(ShoulderPosition.Position.STOW));
          }
        });
    addSequential(new AzimuthCommand(isLeft ? kEjectLeftAzimuth : kEjectRightAzimuth));
    addSequential(
        new TimedDrive(
            1.0, isLeft ? kLeftDrive2 : kRightDrive2, isLeft ? kLeftStrafe2 : kRightStrafe2, 0.0));
    addSequential(new IntakeEject(IntakeSubsystem.Mode.SWITCH_EJECT));
  }

  @Override
  public String toString() {
    return "SwitchCube2Deliver{" + "settings='" + settings + '\'' + '}';
  }
}
