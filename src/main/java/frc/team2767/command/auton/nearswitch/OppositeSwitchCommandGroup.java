package frc.team2767.command.auton.nearswitch;

import frc.team2767.command.StartPosition;
import frc.team2767.command.auton.AzimuthCommand;
import frc.team2767.command.auton.PathCommand;
import frc.team2767.command.auton.PowerUpCommandGroup;
import frc.team2767.command.auton.TimedDrive;
import frc.team2767.command.intake.IntakeEject;
import frc.team2767.command.shoulder.ShoulderPosition;
import frc.team2767.subsystem.IntakeSubsystem;

public class OppositeSwitchCommandGroup extends PowerUpCommandGroup {

  public OppositeSwitchCommandGroup(Side side) {
    super();
    addParallel(new ShoulderPosition(ShoulderPosition.Position.LAUNCH_SWITCH));

    addSequential(new PathCommand(side.path, side.startPosition));
    addSequential(new AzimuthCommand(side.azimuth1));
    addSequential(new TimedDrive(0.75, 0.25, 0.0, 0.0)); // back into switch
    addSequential(new TimedDrive(1.0, 0.0, -0.25 * side.sign, 0.0)); // push blocks
    addSequential(new TimedDrive(0.5, 0.0, 0.25 * side.sign, 0.0)); // go back
    addSequential(new AzimuthCommand(side.azimuth2));
    addSequential(new TimedDrive(0.75, 0.25, 0.0, 0.0)); // back into switch
    addSequential(new IntakeEject(IntakeSubsystem.Mode.FAST_EJECT));
    //    addSequential(new Stow());
  }

  public enum Side {
    LEFT("left_opposite_switch", StartPosition.LEFT, 45.0, 70.0, -1.0),
    RIGHT("right_opposite_switch", StartPosition.RIGHT, -45.0, -70.0, 1.0),
    ;

    private final String path;
    private final StartPosition startPosition;
    private final double azimuth1;
    private final double azimuth2;
    private final double sign;

    Side(String path, StartPosition startPosition, double azimuth1, double azimuth2, double sign) {
      this.path = path;
      this.startPosition = startPosition;
      this.azimuth1 = azimuth1;
      this.azimuth2 = azimuth2;
      this.sign = sign;
    }
  }
}
