package frc.team2767.command.auton;

import static frc.team2767.command.auton.PowerUpGameFeature.SCALE;

import com.moandjiezana.toml.Toml;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;
import frc.team2767.Robot;
import frc.team2767.command.intake.IntakeEject;
import frc.team2767.command.lift.LiftPosition;
import frc.team2767.command.sequence.Stow;
import frc.team2767.command.shoulder.ShoulderPosition;
import frc.team2767.subsystem.IntakeSubsystem;
import java.util.HashMap;
import java.util.Map;
import openrio.powerup.MatchData.OwnedSide;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScaleCube2Deliver extends CommandGroup implements OwnedSidesSettable {
  private static final double EJECT_DURATION = 0.5;
  private static final double SHOULDER_DELAY = 0.5;

  private static final Logger logger = LoggerFactory.getLogger(ScaleCube2Deliver.class);

  private static final Map<Scenario, String> SETTINGS = new HashMap<>();

  static {
    SETTINGS.put(new Scenario(StartPosition.LEFT, SCALE, OwnedSide.LEFT), "L_SC_S_C2D");
    SETTINGS.put(new Scenario(StartPosition.LEFT, SCALE, OwnedSide.RIGHT), "L_SC_O_C2D");
    SETTINGS.put(new Scenario(StartPosition.RIGHT, SCALE, OwnedSide.LEFT), "R_SC_O_C2D");
    SETTINGS.put(new Scenario(StartPosition.RIGHT, SCALE, OwnedSide.RIGHT), "R_SC_S_C2D");
  }

  private final double kLeftEjectAzimuth;
  private final double kRightEjectAzimuth;

  private final Command leftPath;
  private final Command rightPath;

  private String settings;

  ScaleCube2Deliver(StartPosition startPosition) {
    String settings = SETTINGS.get(new Scenario(startPosition, SCALE, OwnedSide.LEFT));
    Toml toml = Robot.INJECTOR.settings().getAutonSettings(settings);
    leftPath = new PathCommand(toml.getString("path")); // auto path azimuth

    kLeftEjectAzimuth = toml.getDouble("ejectAzimuth");

    settings = SETTINGS.get(new Scenario(startPosition, SCALE, OwnedSide.RIGHT));
    toml = Robot.INJECTOR.settings().getAutonSettings(settings);
    rightPath = new PathCommand(toml.getString("path"));

    kRightEjectAzimuth = toml.getDouble("ejectAzimuth");
  }

  @Override
  public void setOwnedSide(StartPosition startPosition, OwnedSide nearSwitch, OwnedSide scale) {
    boolean isLeft = scale == OwnedSide.LEFT;
    settings = SETTINGS.get(new Scenario(startPosition, SCALE, scale));
    addSequential(
        new CommandGroup() {
          {
            addParallel(isLeft ? leftPath : rightPath);
            addSequential(new WaitCommand(SHOULDER_DELAY));
            addSequential(new ShoulderPosition(ShoulderPosition.Position.TIGHT_STOW));
          }

          @Override
          protected void end() {
            logger.trace("PathCommand || (WaitCommand → ShoulderPosition) ENDED");
          }
        });

    addSequential(
        new CommandGroup() {
          {
            addParallel(new AzimuthCommand(isLeft ? kLeftEjectAzimuth : kRightEjectAzimuth));
            addParallel(new LiftPosition(LiftPosition.Position.SCALE_HIGH));
            addParallel(new ShoulderPosition(ShoulderPosition.Position.LAUNCH_SCALE));
          }

          @Override
          protected void end() {
            logger.trace("AzimuthCommand || LiftPosition ENDED");
          }
        });

    addSequential(new IntakeEject(IntakeSubsystem.Mode.SCALE_EJECT, EJECT_DURATION));
    addSequential(new Stow());
  }

  @Override
  public String toString() {
    return "ScaleCube2Deliver{" + "settings='" + settings + '\'' + '}';
  }
}
