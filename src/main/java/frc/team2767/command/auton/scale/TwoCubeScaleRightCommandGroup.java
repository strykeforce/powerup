package frc.team2767.command.auton.scale;

import frc.team2767.command.LogCommand;
import frc.team2767.command.auton.PowerUpCommandGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TwoCubeScaleRightCommandGroup extends PowerUpCommandGroup {

  private static final Logger logger = LoggerFactory.getLogger(TwoCubeScaleRightCommandGroup.class);

  public TwoCubeScaleRightCommandGroup() {
    super();
    addSequential(new LogCommand("START"));
    addSequential(new ScaleCommandGroup(ScaleCommandGroup.Side.RIGHT));
    addSequential(new LogCommand("END PART 1"));
    addSequential(new ScaleSecondCubeRightCommandGroup());
    addSequential(new LogCommand("END PART 2"));
  }
}
