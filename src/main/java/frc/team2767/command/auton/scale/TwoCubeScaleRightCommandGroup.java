package frc.team2767.command.auton.scale;

import frc.team2767.command.auton.PowerUpCommandGroup;

// FIXME: this probably can be generalized like ScaleCommandGroup
public class TwoCubeScaleRightCommandGroup extends PowerUpCommandGroup {

  public TwoCubeScaleRightCommandGroup() {
    addSequential(new ScaleCommandGroup(ScaleCommandGroup.Side.RIGHT));
    addSequential(new ScaleSecondCubeRightCommandGroup());
  }
}
