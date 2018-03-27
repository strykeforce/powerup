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
import frc.team2767.subsystem.DriveSubsystem;
import frc.team2767.subsystem.IntakeSensorsSubsystem;
import frc.team2767.subsystem.IntakeSubsystem;
import java.util.HashMap;
import java.util.Map;
import openrio.powerup.MatchData.OwnedSide;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.swerve.SwerveDrive;

public final class Cube2Fetch extends CommandGroup implements OwnedSidesSettable {

  private static final Logger logger = LoggerFactory.getLogger(Cube2Fetch.class);

  private static final Map<Scenario, String> SETTINGS = new HashMap<>();

  static {
    SETTINGS.put(new Scenario(StartPosition.LEFT, SWITCH, OwnedSide.LEFT), "L_SW_S_C2F");
    SETTINGS.put(new Scenario(StartPosition.LEFT, SWITCH, OwnedSide.RIGHT), "L_SW_O_C2F");
    SETTINGS.put(new Scenario(StartPosition.LEFT, SCALE, OwnedSide.LEFT), "L_SC_S_C2F");
    SETTINGS.put(new Scenario(StartPosition.LEFT, SCALE, OwnedSide.RIGHT), "L_SC_O_C2F");
    SETTINGS.put(new Scenario(StartPosition.RIGHT, SWITCH, OwnedSide.LEFT), "R_SW_O_C2F");
    SETTINGS.put(new Scenario(StartPosition.RIGHT, SWITCH, OwnedSide.RIGHT), "R_SW_S_C2F");
    SETTINGS.put(new Scenario(StartPosition.RIGHT, SCALE, OwnedSide.LEFT), "R_SC_O_C2F");
    SETTINGS.put(new Scenario(StartPosition.RIGHT, SCALE, OwnedSide.RIGHT), "R_SC_S_C2F");
  }

  private final double kLeftIntakeAzimuth;
  private final int kLeftIntakeStopDistance;
  private final int kLeftDriveStopDistance;
  private final double kLeftDrive;
  private final double kLeftStrafe;

  private final double kRightIntakeAzimuth;
  private final int kRightIntakeStopDistance;
  private final int kRightDriveStopDistance;
  private final double kRightDrive;
  private final double kRightStrafe;

  private final Command leftPath;
  private final Command rightPath;
  private final PowerUpGameFeature startFeature;
  private String settings;

  Cube2Fetch(StartPosition startPosition, PowerUpGameFeature startFeature) {
    this.startFeature = startFeature;
    String settings = SETTINGS.get(new Scenario(startPosition, startFeature, OwnedSide.LEFT));
    Toml toml = Robot.INJECTOR.settings().getAutonSettings(settings);
    leftPath =
        new PathCommand(
            toml.getString("path"), startPosition.getPathAngle(toml.getDouble("pathAzimuth")));
    kLeftIntakeAzimuth = toml.getDouble("intakeAzimuth");
    kLeftDrive = toml.getDouble("drive");
    kLeftStrafe = toml.getDouble("strafe");
    kLeftIntakeStopDistance = toml.getLong("intakeStopDistance").intValue();
    kLeftDriveStopDistance = toml.getLong("driveStopDistance").intValue();

    settings = SETTINGS.get(new Scenario(startPosition, startFeature, OwnedSide.RIGHT));
    toml = Robot.INJECTOR.settings().getAutonSettings(settings);
    rightPath =
        new PathCommand(
            toml.getString("path"), startPosition.getPathAngle(toml.getDouble("pathAzimuth")));
    kRightIntakeAzimuth = toml.getDouble("intakeAzimuth");
    kRightDrive = toml.getDouble("drive");
    kRightStrafe = toml.getDouble("strafe");
    kRightIntakeStopDistance = toml.getLong("intakeStopDistance").intValue();
    kRightDriveStopDistance = toml.getLong("driveStopDistance").intValue();
  }

  @Override
  public void setOwnedSide(StartPosition startPosition, OwnedSide nearSwitch, OwnedSide scale) {
    boolean isLeft = (startFeature == SWITCH ? nearSwitch : scale) == OwnedSide.LEFT;
    settings =
        SETTINGS.get(
            new Scenario(startPosition, startFeature, startFeature == SWITCH ? nearSwitch : scale));

    addSequential(
        new CommandGroup() {
          {
            addParallel(isLeft ? leftPath : rightPath);
            addSequential(new Stow());
            addSequential(new WaitCommand(0.5));
            addSequential(new IntakeLoad(IntakeLoad.Position.GROUND), 0.25);
          }

          @Override
          protected void end() {
            logger.trace("PathCommand || (Stow → Wait → IntakeLoad) ENDED");
          }
        });

    addParallel(new EnableLidar());
    addSequential(
        new CommandGroup() {
          {
            addParallel(new AzimuthCommand(isLeft ? kLeftIntakeAzimuth : kRightIntakeAzimuth));
          }

          @Override
          protected void end() {
            logger.trace("AzimuthCommand || IntakeLoad ENDED");
          }
        });

    addSequential(
        new CommandGroup() {
          {
            addParallel(
                new IntakeInCubeTwo(isLeft ? kLeftIntakeStopDistance : kRightIntakeStopDistance),
                3.0);
            addParallel(
                isLeft
                    ? new DriveToCube(kLeftDriveStopDistance, kLeftDrive, kLeftStrafe)
                    : new DriveToCube(kRightDriveStopDistance, kRightDrive, kRightStrafe));
          }

          @Override
          protected void end() {
            logger.trace("IntakeInCubeTwo || DriveToCube ENDED");
          }
        });

    addParallel(new DisableLidar());
    addSequential(new StartIntakeHold());
  }

  @Override
  public String toString() {
    return "Cube2Fetch{" + "settings='" + settings + '\'' + '}';
  }

  static final class DriveToCube extends Command {

    private final DriveSubsystem driveSubsystem = Robot.INJECTOR.driveSubsystem();
    private final IntakeSensorsSubsystem intakeSensorsSubsystem =
        Robot.INJECTOR.intakeSensorsSubsystem();

    private final double forward;
    private final double strafe;
    private final int distance;

    DriveToCube(int distance, double forward, double strafe) {
      this.distance = distance;
      this.forward = forward;
      this.strafe = strafe;
      requires(driveSubsystem);
    }

    @Override
    protected void initialize() {
      driveSubsystem.setDriveMode(SwerveDrive.DriveMode.CLOSED_LOOP);
      driveSubsystem.drive(-forward, strafe, 0d);
      logger.info(
          "driving to cube, lidar distance = {}", intakeSensorsSubsystem.getLidarDistance());
    }

    @Override
    protected boolean isFinished() {
      return intakeSensorsSubsystem.isLidarDisanceWithin(distance);
    }

    @Override
    protected void end() {
      driveSubsystem.stop();
      logger.info(
          "stopped driving to cube, lidar distance = {}",
          intakeSensorsSubsystem.getLidarDistance());
      logger.trace("DriveToCube ENDED");
    }
  }

  static final class IntakeInCubeTwo extends Command {

    private final IntakeSubsystem intakeSubsystem = Robot.INJECTOR.intakeSubsystem();
    private final IntakeSensorsSubsystem intakeSensorsSubsystem =
        Robot.INJECTOR.intakeSensorsSubsystem();
    private final int distance;

    IntakeInCubeTwo(int distance) {
      this.distance = distance;
      requires(intakeSubsystem);
    }

    @Override
    protected void initialize() {
      intakeSubsystem.run(IntakeSubsystem.Mode.LOAD);
      logger.info("intake running, lidar distance = {}", intakeSensorsSubsystem.getLidarDistance());
    }

    @Override
    protected boolean isFinished() {
      return intakeSensorsSubsystem.isLidarDisanceWithin(distance);
    }

    @Override
    protected void end() {
      intakeSubsystem.stop();
      logger.info("intake stopped, lidar distance = {}", intakeSensorsSubsystem.getLidarDistance());
      logger.trace("IntakeInCubeTwo ENDED");
    }
  }
}
