package frc.team2767.command.auton;

import static frc.team2767.command.auton.PowerUpGameFeature.SCALE;
import static frc.team2767.command.auton.PowerUpGameFeature.SWITCH;

import com.moandjiezana.toml.Toml;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;
import frc.team2767.Robot;
import frc.team2767.command.intake.DisableLidar;
import frc.team2767.command.intake.EnableLidar;
import frc.team2767.command.intake.IntakeLoad;
import frc.team2767.command.intake.StartIntakeHold;
import frc.team2767.command.sequence.Stow;
import frc.team2767.command.shoulder.ShoulderPosition;
import frc.team2767.command.vision.LightsOff;
import frc.team2767.command.vision.LightsOn;
import frc.team2767.subsystem.DriveSubsystem;
import frc.team2767.subsystem.IntakeSensorsSubsystem;
import frc.team2767.subsystem.IntakeSubsystem;
import java.util.HashMap;
import java.util.Map;
import openrio.powerup.MatchData.OwnedSide;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Cube3Fetch extends CommandGroup implements OwnedSidesSettable {
  {
  }

  static final Logger logger = LoggerFactory.getLogger(Cube2Fetch.class);
  private static final Map<Scenario, String> SETTINGS = new HashMap<>();

  static {
    SETTINGS.put(new Scenario(StartPosition.LEFT, SCALE, OwnedSide.LEFT), "L_SC_S_C3F");
    SETTINGS.put(new Scenario(StartPosition.LEFT, SCALE, OwnedSide.RIGHT), "L_SC_O_C3F");
    SETTINGS.put(new Scenario(StartPosition.RIGHT, SCALE, OwnedSide.LEFT), "R_SC_O_C3F");
    SETTINGS.put(new Scenario(StartPosition.RIGHT, SCALE, OwnedSide.RIGHT), "R_SC_S_C3F");

    SETTINGS.put(new Scenario(StartPosition.LEFT, SWITCH, OwnedSide.LEFT), "L_SW_S_C3F");
    SETTINGS.put(new Scenario(StartPosition.LEFT, SWITCH, OwnedSide.RIGHT), "L_SW_O_C3F");
    SETTINGS.put(new Scenario(StartPosition.RIGHT, SWITCH, OwnedSide.RIGHT), "R_SW_S_C3F");
    SETTINGS.put(new Scenario(StartPosition.RIGHT, SWITCH, OwnedSide.LEFT), "R_SW_O_C3F");
  }

  private final DriveSubsystem driveSubsystem = Robot.INJECTOR.driveSubsystem();
  private boolean isLeft;
  private boolean isCross;

  private int kLeftIntakeStopDistance;
  private int kLeftDriveStopDistance;

  private int kRightIntakeStopDistance;
  private int kRightDriveStopDistance;

  private PowerUpGameFeature startFeature;
  private String settings;
  private DriveToCube driveToCube;

  private double kLeftDirection;
  private int kLeftDistance;
  private double kLeftAzimuth;
  private double kRightDirection;
  private int kRightDistance;
  private double kRightAzimuth;
  private double kRightDriveMultiplier;
  private double kLeftDriveMultiplier;

  Cube3Fetch(StartPosition startPosition, PowerUpGameFeature startFeature) {
    // if (startFeature == SWITCH) return; // don't currently get second cube after switch cube 1
    this.startFeature = startFeature;
    String settings = SETTINGS.get(new Scenario(startPosition, startFeature, OwnedSide.LEFT));
    Toml toml = Robot.INJECTOR.settings().getAutonSettings(settings);
    kLeftIntakeStopDistance = toml.getLong("intakeStopDistance").intValue();
    kLeftDriveStopDistance = toml.getLong("driveStopDistance").intValue();
    kLeftDirection = toml.getDouble("direction");
    kLeftDistance = toml.getLong("distance").intValue();
    kLeftAzimuth = toml.getDouble("azimuth");
    kLeftDriveMultiplier = toml.getDouble("multiplier");

    settings = SETTINGS.get(new Scenario(startPosition, startFeature, OwnedSide.RIGHT));
    toml = Robot.INJECTOR.settings().getAutonSettings(settings);
    kRightIntakeStopDistance = toml.getLong("intakeStopDistance").intValue();
    kRightDriveStopDistance = toml.getLong("driveStopDistance").intValue();
    kRightDirection = toml.getDouble("direction");
    kRightDistance = toml.getLong("distance").intValue();
    kRightAzimuth = toml.getDouble("azimuth");
    kRightDriveMultiplier = toml.getDouble("multiplier");
  }

  @Override
  public void setOwnedSide(StartPosition startPosition, OwnedSide nearSwitch, OwnedSide scale) {
    isLeft = (startFeature == SWITCH ? nearSwitch : scale) == OwnedSide.LEFT;
    isCross =
        (!isLeft || startPosition != StartPosition.LEFT)
            && (isLeft || startPosition != StartPosition.RIGHT);
    logger.debug("isLeft = {}, isCross = {}", isLeft, isCross);
    AzimuthToCube azimuthToCube = new AzimuthToCube();
    settings =
        SETTINGS.get(
            new Scenario(startPosition, startFeature, startFeature == SWITCH ? nearSwitch : scale));

    addSequential(
        new CommandGroup() {
          {
            addParallel(
                isLeft
                    ? new MotionDrive(kLeftDirection, kLeftDistance, kLeftAzimuth)
                    : new MotionDrive(kRightDirection, kRightDistance, kRightAzimuth));
            addParallel(new EnableLidar());
            addParallel(new LightsOn());
            addSequential(new Stow(), 1.2);
            addSequential(new WaitCommand(0.5));
            addSequential(new IntakeLoad(IntakeLoad.Position.GROUND), 0.25);
          }

          @Override
          protected void end() {
            logger.trace("PathCommand || (Stow → Wait → IntakeLoad) ENDED");
          }
        });

    addSequential(azimuthToCube);

    driveToCube =
        isLeft
            ? new DriveToCube(kLeftDriveStopDistance, isLeft, isCross)
            : new DriveToCube(kRightDriveStopDistance, isLeft, isCross);

    addSequential(
        new CommandGroup() {
          {
            addParallel(
                new Cube2Fetch.IntakeInCubeTwo(
                    isLeft ? kLeftIntakeStopDistance : kRightIntakeStopDistance),
                3.0);
            addParallel(driveToCube);
          }

          @Override
          protected void end() {
            logger.trace("IntakeInCubeTwo || DriveToCube ENDED");
          }
        });

    addParallel(new DisableLidar());
    addParallel(new LightsOff());
    addSequential(new StartIntakeHold());

    addParallel(
        new DriveFromCube(driveToCube, isLeft ? kLeftDriveMultiplier : kRightDriveMultiplier));
    addSequential(new WaitCommand(0.30));
    addSequential(new ShoulderPosition(ShoulderPosition.Position.TIGHT_STOW));
  }

  @Override
  public String toString() {
    return "Cube2Fetch{" + "settings='" + settings + '\'' + '}';
  }

  static final class IntakeInCubeTwo extends Command {

    private final IntakeSubsystem intakeSubsystem = Robot.INJECTOR.intakeSubsystem();
    private final IntakeSensorsSubsystem intakeSensorsSubsystem =
        Robot.INJECTOR.intakeSensorsSubsystem();
    private final int targetDistance;

    IntakeInCubeTwo(int targetDistance) {
      this.targetDistance = targetDistance;
      requires(intakeSubsystem);
    }

    @Override
    protected void initialize() {
      intakeSubsystem.run(IntakeSubsystem.Mode.LOAD);
      logger.info(
          "intake running, lidar targetDistance = {}", intakeSensorsSubsystem.getLidarDistance());
    }

    @Override
    protected boolean isFinished() {
      return intakeSensorsSubsystem.isLidarDisanceWithin(targetDistance);
    }

    @Override
    protected void end() {
      intakeSubsystem.stop();
      logger.info(
          "intake stopped, lidar targetDistance = {}", intakeSensorsSubsystem.getLidarDistance());
      logger.trace("IntakeInCubeTwo ENDED");
    }
  }
}
