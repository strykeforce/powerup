package frc.team2767.command.auton.scale;

import frc.team2767.command.StartPosition;
import frc.team2767.command.auton.PowerUpCommandGroup;

// FIXME: this probably can be generalized like ScaleCommandGroup
public class TwoCubeScaleRightCommandGroup extends PowerUpCommandGroup {

  public TwoCubeScaleRightCommandGroup() {
    addSequential(new ScaleCommandGroup(ScaleSettings.getInstance(StartPosition.RIGHT)));
    addSequential(
        new ScaleSecondCubeRightCommandGroup(ScaleSettings.getInstance(StartPosition.RIGHT)));
  }
}
