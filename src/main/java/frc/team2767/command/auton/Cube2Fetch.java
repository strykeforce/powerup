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
  private static boolean isLeft;

  static {
    //    SETTINGS.put(new Scenario(StartPosition.LEFT, SWITCH, OwnedSide.LEFT), "L_SW_S_C2F");
    //    SETTINGS.put(new Scenario(StartPosition.LEFT, SWITCH, OwnedSide.RIGHT), "L_SW_O_C2F");
    SETTINGS.put(new Scenario(StartPosition.LEFT, SCALE, OwnedSide.LEFT), "L_SC_S_C2F");
    SETTINGS.put(new Scenario(StartPosition.LEFT, SCALE, OwnedSide.RIGHT), "L_SC_O_C2F");
    //    SETTINGS.put(new Scenario(StartPosition.RIGHT, SWITCH, OwnedSide.LEFT), "R_SW_O_C2F");
    //    SETTINGS.put(new Scenario(StartPosition.RIGHT, SWITCH, OwnedSide.RIGHT), "R_SW_S_C2F");
    SETTINGS.put(new Scenario(StartPosition.RIGHT, SCALE, OwnedSide.LEFT), "R_SC_O_C2F");
    SETTINGS.put(new Scenario(StartPosition.RIGHT, SCALE, OwnedSide.RIGHT), "R_SC_S_C2F");
  }

  private final DriveSubsystem driveSubsystem = Robot.INJECTOR.driveSubsystem();
  private double kLeftIntakeAzimuth;
  private int kLeftIntakeStopDistance;
  private int kLeftDriveStopDistance;
  private double kLeftForward;
  private double kLeftStrafe;

  private double kRightIntakeAzimuth;
  private int kRightIntakeStopDistance;
  private int kRightDriveStopDistance;
  private double kRightForward;
  private double kRightStrafe;
  private double kDrive;

  private Command leftPath;
  private Command rightPath;
  private PowerUpGameFeature startFeature;
  private String settings;
  private AzimuthToCube azimuthToCube;

  Cube2Fetch(StartPosition startPosition, PowerUpGameFeature startFeature) {
    if (startFeature == SWITCH) return; // don't currently get second cube after switch cube 1
    this.startFeature = startFeature;
    String settings = SETTINGS.get(new Scenario(startPosition, startFeature, OwnedSide.LEFT));
    Toml toml = Robot.INJECTOR.settings().getAutonSettings(settings);
    leftPath = new PathCommand(toml.getString("path")); // auto path azimuth
    kLeftIntakeAzimuth = toml.getDouble("intakeAzimuth");
    kLeftForward = toml.getDouble("drive");
    kLeftStrafe = toml.getDouble("strafe");
    kLeftIntakeStopDistance = toml.getLong("intakeStopDistance").intValue();
    kLeftDriveStopDistance = toml.getLong("driveStopDistance").intValue();

    settings = SETTINGS.get(new Scenario(startPosition, startFeature, OwnedSide.RIGHT));
    toml = Robot.INJECTOR.settings().getAutonSettings(settings);
    rightPath = new PathCommand(toml.getString("path"));
    kRightIntakeAzimuth = toml.getDouble("intakeAzimuth");
    kRightForward = toml.getDouble("drive");
    kRightStrafe = toml.getDouble("strafe");
    kRightIntakeStopDistance = toml.getLong("intakeStopDistance").intValue();
    kRightDriveStopDistance = toml.getLong("driveStopDistance").intValue();
    kDrive = 0.15;
  }

  @Override
  public void setOwnedSide(StartPosition startPosition, OwnedSide nearSwitch, OwnedSide scale) {
    isLeft = (startFeature == SWITCH ? nearSwitch : scale) == OwnedSide.LEFT;
    azimuthToCube = new AzimuthToCube(startPosition);
    settings =
        SETTINGS.get(
            new Scenario(startPosition, startFeature, startFeature == SWITCH ? nearSwitch : scale));

    addSequential(
        new CommandGroup() {
          {
            addParallel(isLeft ? leftPath : rightPath);
            addSequential(new Stow(), 1.2);
            addSequential(new WaitCommand(0.25));
            addSequential(new IntakeLoad(IntakeLoad.Position.GROUND), 0.25);
          }

          @Override
          protected void end() {
            logger.trace("PathCommand || (Stow → Wait → IntakeLoad) ENDED");
          }
        });

    addParallel(new EnableLidar());
    addSequential(new AzimuthCommand(isLeft ? kLeftIntakeAzimuth : kRightIntakeAzimuth));

    addSequential(new WaitCommand(1));

    addSequential(azimuthToCube);

    addSequential(
        new CommandGroup() {
          {
            addParallel(
                new IntakeInCubeTwo(isLeft ? kLeftIntakeStopDistance : kRightIntakeStopDistance),
                3.0);
            addParallel(
                isLeft
                    ? new DriveToCube(kLeftDriveStopDistance)
                    : new DriveToCube(kRightDriveStopDistance));
          }

          @Override
          protected void end() {
            logger.trace("IntakeInCubeTwo || DriveToCube ENDED");
          }
        });

    addParallel(new DisableLidar());
    addSequential(new StartIntakeHold());
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

  final class DriveToCube extends Command {

    private final DriveSubsystem driveSubsystem = Robot.INJECTOR.driveSubsystem();
    private final IntakeSensorsSubsystem intakeSensorsSubsystem =
        Robot.INJECTOR.intakeSensorsSubsystem();
    private final int distance;

    DriveToCube(int distance) {
      this.distance = distance;
      requires(driveSubsystem);
    }

    @Override
    protected void initialize() {
      driveSubsystem.setDriveMode(SwerveDrive.DriveMode.CLOSED_LOOP);
      //      driveSubsystem.drive(-forward, strafe, 0d);

      logger.debug("Current Yaw = {}" + driveSubsystem.getGyro().getYaw());
      logger.debug(
          "Left Forward = {}",
          (kDrive * Math.sin(Math.toRadians(90 - driveSubsystem.getGyro().getYaw()))));
      logger.debug(
          "Left Strafe = {}",
          (-1 * kDrive * Math.cos(Math.toRadians(90 - driveSubsystem.getGyro().getYaw()))));
      logger.debug(
          "Right Forward = {}",
          (-1 * kDrive * Math.sin(Math.toRadians(-90 - driveSubsystem.getGyro().getYaw()))));
      logger.debug(
          "Right Strafe = {}",
          (-1 * kDrive * Math.cos(Math.toRadians(-90 - driveSubsystem.getGyro().getYaw()))));

      logger.info(
          "driving to cube, lidar distance = {}", intakeSensorsSubsystem.getLidarDistance());

      if (isLeft) {
        driveSubsystem.drive(
            -1 * kDrive * Math.cos(Math.toRadians(90 - driveSubsystem.getGyro().getYaw())),
            kDrive * Math.sin(Math.toRadians(90 - driveSubsystem.getGyro().getYaw())),
            0.0);
      } else {
        driveSubsystem.drive(
            kDrive * Math.sin(Math.toRadians(-90 - driveSubsystem.getGyro().getYaw())),
            -1 * kDrive * Math.cos(Math.toRadians(-90 - driveSubsystem.getGyro().getYaw())),
            0.0);
      }
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

  @Override
  public String toString() {
    return "Cube2Fetch{" + "settings='" + settings + '\'' + '}';
  }
}
