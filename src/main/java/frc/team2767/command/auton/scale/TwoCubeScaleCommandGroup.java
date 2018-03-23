package frc.team2767.command.auton.scale;

import frc.team2767.command.auton.PowerUpCommandGroup;

// FIXME: this probably can be generalized like ScaleCommandGroup
public class TwoCubeScaleCommandGroup extends PowerUpCommandGroup {

  public TwoCubeScaleCommandGroup(ScaleSettings scaleSettings) {
    addSequential(new ScaleCommandGroup(scaleSettings));
    addSequential(new ScaleSecondCubeCommandGroup(scaleSettings));
  }
}
