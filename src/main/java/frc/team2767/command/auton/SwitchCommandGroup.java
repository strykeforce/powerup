package frc.team2767.command.auton;

import frc.team2767.command.intake.IntakeEject;
import frc.team2767.command.sequence.Stow;
import frc.team2767.command.shoulder.ShoulderPosition;
import frc.team2767.subsystem.IntakeSubsystem;

public class SwitchCommandGroup extends PowerUpCommandGroup {

  public SwitchCommandGroup(Side side) {
    super();
    addParallel(new ShoulderPosition(ShoulderPosition.Position.LAUNCH_SWITCH));

    addSequential(new PathCommand(side.path));
    addSequential(new AzimuthCommand(side.azimuth));

    addSequential(new IntakeEject(IntakeSubsystem.Mode.FAST_EJECT));
    addSequential(new Stow());
  }

  public enum Side {
    LEFT("left_switch", 90.0),
    RIGHT("right_switch", -90.0),
    ;

    private final String path;
    private final double azimuth;

    Side(String path, double azimuth) {
      this.path = path;
      this.azimuth = azimuth;
    }
  }
}
