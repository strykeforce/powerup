package frc.team2767.command.auton;

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
import openrio.powerup.MatchData.GameFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.swerve.SwerveDrive;

public class Cube2Fetch extends CommandGroup {

  private static final Logger logger = LoggerFactory.getLogger(Cube2Fetch.class);

  private final String kPath;
  private final double kPathAzimuth;
  private final double kIntakeAzimuth;
  private final int kIntakeStopDistance;
  private final int kDriveStopDistance;
  private final double kDrive;
  private final double kStrafe;

  public Cube2Fetch(StartPosition startPosition, GameFeature startFeature) {
    String settings;
    // FIXME
    if (startFeature == GameFeature.SCALE)
      settings = startPosition == StartPosition.RIGHT ? "R_SC_O_C2F" : "L_SC_O_C2F";
    else settings = startPosition == StartPosition.RIGHT ? "R_SW_C2F" : "L_SW_C2F";

    Toml toml = Robot.INJECTOR.settings().getAutonSettings(settings);
    kPath = toml.getString("path");
    kPathAzimuth = toml.getDouble("pathAzimuth");
    kIntakeAzimuth = toml.getDouble("intakeAzimuth");
    kDrive = toml.getDouble("drive");
    kStrafe = toml.getDouble("strafe");
    kIntakeStopDistance = toml.getLong("intakeStopDistance").intValue();
    kDriveStopDistance = toml.getLong("driveStopDistance").intValue();

    addParallel(new EnableLidar());

    addSequential(
        new CommandGroup() {
          {
            addParallel(new PathCommand(kPath, startPosition.getPathAngle(kPathAzimuth)));
            addSequential(new Stow());
            addSequential(new WaitCommand(0.5));
            addSequential(new IntakeLoad(IntakeLoad.Position.GROUND), 0.25);
          }

          @Override
          protected void end() {
            logger.trace("PathCommand || (Stow → Wait → IntakeLoad) ENDED");
          }
        });

    addSequential(
        new CommandGroup() {
          {
            addParallel(new AzimuthCommand(kIntakeAzimuth));
          }

          @Override
          protected void end() {
            logger.trace("AzimuthCommand || IntakeLoad ENDED");
          }
        });

    addSequential(
        new CommandGroup() {
          {
            addParallel(new IntakeInCubeTwo(kIntakeStopDistance), 3.0);
            addParallel(new DriveToCube(kDriveStopDistance, kDrive, kStrafe));
          }

          @Override
          protected void end() {
            logger.trace("IntakeInCubeTwo || DriveToCube ENDED");
          }
        });

    addParallel(new DisableLidar());
    addSequential(new StartIntakeHold());
  }

  private static class DriveToCube extends Command {

    private final DriveSubsystem driveSubsystem = Robot.INJECTOR.driveSubsystem();
    private final IntakeSensorsSubsystem intakeSensorsSubsystem =
        Robot.INJECTOR.intakeSensorsSubsystem();

    private final double forward;
    private final double strafe;
    private final int distance;

    public DriveToCube(int distance, double forward, double strafe) {
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

  private static class IntakeInCubeTwo extends Command {

    private final IntakeSubsystem intakeSubsystem = Robot.INJECTOR.intakeSubsystem();
    private final IntakeSensorsSubsystem intakeSensorsSubsystem =
        Robot.INJECTOR.intakeSensorsSubsystem();
    private final int distance;

    public IntakeInCubeTwo(int distance) {
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
