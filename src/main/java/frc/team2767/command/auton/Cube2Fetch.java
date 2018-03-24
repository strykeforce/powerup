package frc.team2767.command.auton;

import com.moandjiezana.toml.Toml;
import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.Robot;
import frc.team2767.command.StartPosition;
import frc.team2767.command.auton.scale.DriveToCube;
import frc.team2767.command.auton.scale.IntakeInCubeTwo;
import frc.team2767.command.intake.DisableLidar;
import frc.team2767.command.intake.EnableLidar;
import frc.team2767.command.intake.IntakeLoad;
import frc.team2767.command.intake.StartIntakeHold;
import frc.team2767.command.sequence.Stow;
import openrio.powerup.MatchData.GameFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            addParallel(new Stow(), 1.0);
            addParallel(new PathCommand(kPath, kPathAzimuth));
          }

          @Override
          protected void end() {
            logger.trace("Stow || PathCommand ENDED");
          }
        });

    addSequential(
        new CommandGroup() {
          {
            addParallel(new AzimuthCommand(kIntakeAzimuth));
            addParallel(new IntakeLoad(IntakeLoad.Position.GROUND), 0.25);
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
}
