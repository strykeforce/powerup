package frc.team2767.command.auton;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.motion.PathController;

public class WaitForDistance extends Command {

  private final PathController pathController;
  private final int distance;

  public WaitForDistance(PathController pathController, int distance) {
    this.pathController = pathController;
    this.distance = distance;
  }

  @Override
  protected boolean isFinished() {
    return pathController.getDistance() > distance;
  }
}
