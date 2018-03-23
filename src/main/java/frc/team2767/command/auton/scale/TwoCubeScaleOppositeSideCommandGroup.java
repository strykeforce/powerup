package frc.team2767.command.auton.scale;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class TwoCubeScaleOppositeSideCommandGroup extends CommandGroup {

  public TwoCubeScaleOppositeSideCommandGroup(
      OppositeScaleCommandGroup.Side side, ScaleSettings scaleSettings) {
    addSequential(new OppositeScaleCommandGroup(side));
    addSequential(new ScaleSecondCubeCommandGroup(scaleSettings));
  }
}
