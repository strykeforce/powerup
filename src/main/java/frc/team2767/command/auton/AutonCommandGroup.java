package frc.team2767.command.auton;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.Robot;
import frc.team2767.command.test.PathCommand;
import openrio.powerup.MatchData;

/** Command group for autonomous mode. */
public class AutonCommandGroup extends CommandGroup {

  private static final int LEFT = 1;
  private static final int CENTER_LEFT = 2;
  private static final int CENTER_RIGHT = 3;
  private static final int CENTER_RIGHT_EXCHANGE = 4;

  public AutonCommandGroup() {
    if (Robot.getNearSwitch().equals(MatchData.OwnedSide.LEFT)) {
      addSequential(new PathCommand(CENTER_LEFT));
    } else if (Robot.getNearSwitch().equals(MatchData.OwnedSide.LEFT)) {
      addSequential(new PathCommand(CENTER_RIGHT));
    }
  }
}
