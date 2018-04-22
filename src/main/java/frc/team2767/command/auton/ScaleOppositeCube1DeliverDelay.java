package frc.team2767.command.auton;

import com.moandjiezana.toml.Toml;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;
import frc.team2767.Robot;
import frc.team2767.command.intake.EnableLidar;
import frc.team2767.command.intake.IntakeEject;
import frc.team2767.command.lift.LiftPosition;
import frc.team2767.command.shoulder.ShoulderPosition;
import frc.team2767.subsystem.IntakeSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScaleOppositeCube1DeliverDelay extends CommandGroup {

  private static final double EJECT_DURATION = 0.5;
  private static final double START_POSITION_YAW = 0d;

  private static final Logger logger =
      LoggerFactory.getLogger(ScaleOppositeCube1DeliverDelay.class);
  private final String settings;

  public ScaleOppositeCube1DeliverDelay(StartPosition startPosition) {
    settings = startPosition == StartPosition.RIGHT ? "R_SC_O_C1D_D" : "L_SC_O_C1D_D";
    Toml toml = Robot.INJECTOR.settings().getAutonSettings(settings);
    String kPath = toml.getString("path");
    double kWait = toml.getDouble("wait");
    double kDirection1 = toml.getDouble("direction1");
    int kDistance1 = toml.getLong("distance1").intValue();
    double kAzimuth1 = toml.getDouble("azimuth1");
    double kDirection2 = toml.getDouble("direction2");
    int kDistance2 = toml.getLong("distance2").intValue();
    double kAzimuth2 = toml.getDouble("azimuth2");

    addParallel(new EnableLidar());

    addParallel(new ShoulderPosition(ShoulderPosition.Position.TIGHT_STOW));
    addSequential(new PathCommand(kPath, startPosition.getPathAngle(START_POSITION_YAW)));
    addSequential(new WaitCommand(kWait));
    addSequential(new MotionDrive(kDirection1, kDistance1, kAzimuth1));
    addSequential(
        new CommandGroup() {
          {
            addParallel(new LiftPosition(LiftPosition.Position.SCALE_HIGH));
            addParallel(new ShoulderPosition(ShoulderPosition.Position.LAUNCH_SCALE));
            addParallel(new AzimuthCommand(kAzimuth2));
          }
        });
    addSequential(new MotionDrive(kDirection2, kDistance2, kAzimuth2));
    addSequential(new IntakeEject(IntakeSubsystem.Mode.SLOW_EJECT, EJECT_DURATION));
  }

  @Override
  public String toString() {
    return "ScaleOppositeCube1DeliverDelay{" + "settings='" + settings + '\'' + '}';
  }
}
