package frc.team2767.command.auton.scale;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TwoCubeScaleRightCommandGroup extends CommandGroup {

  private static final Logger logger = LoggerFactory.getLogger(TwoCubeScaleRightCommandGroup.class);

  public TwoCubeScaleRightCommandGroup() {
    addSequential(new ScaleCommandGroup(ScaleCommandGroup.Side.RIGHT));
    addSequential(new ScaleSecondCubeRightCommandGroup());
  }
}
