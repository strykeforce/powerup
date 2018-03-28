package frc.team2767.command.auton;

import com.moandjiezana.toml.Toml;
import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.Robot;
import frc.team2767.command.intake.IntakeEject;
import frc.team2767.command.shoulder.ShoulderPosition;
import frc.team2767.subsystem.IntakeSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SwitchOppositeCube1Deliver extends CommandGroup {

  private static final double EJECT_DURATION = 0.5;
  private static final double START_POSITION_YAW = 0d;

  private static final Logger logger = LoggerFactory.getLogger(SwitchOppositeCube1Deliver.class);
  private final String settings;

  public SwitchOppositeCube1Deliver(StartPosition startPosition) {
    settings = startPosition == StartPosition.RIGHT ? "R_SW_O_C1D" : "L_SW_O_C1D";
    Toml toml = Robot.INJECTOR.settings().getAutonSettings(settings);
    String kPath = toml.getString("path");
    double kAzimuth1 = toml.getDouble("azimuth1");
    double kAzimuth2 = toml.getDouble("azimuth2");
    double kTimeout1 = toml.getDouble("timeout1");
    double kTimeout2 = toml.getDouble("timeout2");
    double kTimeout3 = toml.getDouble("timeout3");
    double kTimeout4 = toml.getDouble("timeout4");
    double kDrive1 = toml.getDouble("drive1");
    double kDrive2 = toml.getDouble("drive2");
    double kDrive3 = toml.getDouble("drive3");
    double kDrive4 = toml.getDouble("drive4");
    double kStrafe1 = toml.getDouble("strafe1");
    double kStrafe2 = toml.getDouble("strafe2");
    double kStrafe3 = toml.getDouble("strafe3");
    double kStrafe4 = toml.getDouble("strafe4");

    addParallel(new ShoulderPosition(ShoulderPosition.Position.TIGHT_STOW));
    addSequential(new PathCommand(kPath, startPosition.getPathAngle(START_POSITION_YAW)));

    addSequential(new AzimuthCommand(kAzimuth1));
    addSequential(new TimedDrive(kTimeout1, kDrive1, kStrafe1, 0.0)); // back into switch
    addSequential(new TimedDrive(kTimeout2, kDrive2, kStrafe2, 0.0)); // push blocks
    addSequential(new TimedDrive(kTimeout3, kDrive3, kStrafe3, 0.0)); // go back
    addSequential(new TimedDrive(kTimeout4, kDrive4, kStrafe4, 0.0)); // back into switch
    addSequential(new AzimuthCommand(kAzimuth2), 1.0);
    addSequential(new IntakeEject(IntakeSubsystem.Mode.SWITCH_EJECT, EJECT_DURATION));
  }

  @Override
  public String toString() {
    return "SwitchOppositeCube1Deliver{" + "settings='" + settings + '\'' + '}';
  }
}
