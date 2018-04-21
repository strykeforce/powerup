package frc.team2767.command.auton;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.motion.PathController;

public class WaitForDistance extends Command {

  private final PathController pathController;
  private final int distance;
  private int goalDistance;

  public WaitForDistance(PathController pathController, int distance) {
    this.pathController = pathController;
    this.distance = distance;
  }

  @Override
  protected void initialize() {
    goalDistance = (int) Math.round(pathController.getTicks()) - distance;
  }

  @Override
  protected boolean isFinished() {
    return pathController.getDistance() > goalDistance;
  }
}
