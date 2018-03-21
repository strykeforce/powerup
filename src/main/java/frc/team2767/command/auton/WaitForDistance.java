package frc.team2767.command.auton;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.command.auton.scale.ScaleCommandGroup;
import frc.team2767.motion.PathController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WaitForDistance extends Command {
  private static final Logger logger = LoggerFactory.getLogger(ScaleCommandGroup.class);

  private final PathController pathController;
  private final int distance;

  public WaitForDistance(PathController pathController, int distance) {
    this.pathController = pathController;
    this.distance = distance;
  }

  @Override
  protected void end() {
    logger.debug("WaitForDistance ENDED");
  }

  @Override
  protected boolean isFinished() {
    return pathController.getDistance() > distance;
  }
}
