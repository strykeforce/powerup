package frc.team2767.command.auton;

import com.moandjiezana.toml.Toml;
import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.Robot;
import frc.team2767.command.intake.IntakeEject;
import frc.team2767.command.lift.LiftPosition;
import frc.team2767.command.sequence.Stow;
import frc.team2767.command.shoulder.ShoulderPosition;
import frc.team2767.subsystem.IntakeSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScaleOppositeCube1Deliver extends CommandGroup {

  private static final Logger logger = LoggerFactory.getLogger(ScaleSameCube1Deliver.class);

  public ScaleOppositeCube1Deliver(StartPosition startPosition) {
    String settings = startPosition == StartPosition.RIGHT ? "R_SC_O_C1D" : "L_SC_O_C1D";
    Toml toml = Robot.INJECTOR.settings().getAutonSettings(settings);
    String kPath = toml.getString("path");
    double kAzimuth = toml.getDouble("azimuth");

    addParallel(new ShoulderPosition(ShoulderPosition.Position.TIGHT_STOW));
    addSequential(new PathCommand(kPath, startPosition.getPathAngle(0)));

    addSequential(
        new CommandGroup() {
          {
            addParallel(new AzimuthCommand(kAzimuth));
            addParallel(new LiftPosition(LiftPosition.Position.SCALE_HIGH));
            addParallel(new ShoulderPosition(ShoulderPosition.Position.LAUNCH_SCALE));
          }
        });

    addSequential(new IntakeEject(IntakeSubsystem.Mode.SCALE_EJECT));
    addSequential(new Stow());
  }
}
