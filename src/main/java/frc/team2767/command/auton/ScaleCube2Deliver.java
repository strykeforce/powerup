package frc.team2767.command.auton;

import com.moandjiezana.toml.Toml;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;
import frc.team2767.Robot;
import frc.team2767.command.StartPosition;
import frc.team2767.command.intake.IntakeEject;
import frc.team2767.command.lift.LiftPosition;
import frc.team2767.command.sequence.Stow;
import frc.team2767.command.shoulder.ShoulderPosition;
import frc.team2767.subsystem.IntakeSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScaleCube2Deliver extends CommandGroup {

  private static final Logger logger = LoggerFactory.getLogger(ScaleCube2Deliver.class);

  private final String kPath;
  private final double kPathAzimuth;
  private final double kEjectAzimuth;

  public ScaleCube2Deliver(StartPosition startPosition) {
    // FIXME
    String settings = startPosition == StartPosition.RIGHT ? "R_SC_O_C2D" : "L_SC_O_C2D";
    Toml toml = Robot.INJECTOR.settings().getAutonSettings(settings);
    kPath = toml.getString("path");
    kPathAzimuth = toml.getDouble("pathAzimuth");
    kEjectAzimuth = toml.getDouble("ejectAzimuth");

    addSequential(
        new CommandGroup() {
          {
            addParallel(new PathCommand(kPath, kPathAzimuth));
            addSequential(new WaitCommand(0.5));
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
            addParallel(new AzimuthCommand(kEjectAzimuth));
            addParallel(new LiftPosition(LiftPosition.Position.SCALE_HIGH));
          }

          @Override
          protected void end() {
            logger.trace("AzimuthCommand || LiftPosition ENDED");
          }
        });

    addSequential(new ShoulderPosition(ShoulderPosition.Position.LAUNCH_SCALE));
    addSequential(new IntakeEject(IntakeSubsystem.Mode.SCALE_EJECT));
    addSequential(new Stow());
  }
}
