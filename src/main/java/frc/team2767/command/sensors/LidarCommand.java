package frc.team2767.command.sensors;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Robot;
import frc.team2767.subsystem.LidarSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LidarCommand extends Command {

  private static final Logger logger = LoggerFactory.getLogger(LidarCommand.class);
  private LidarSubsystem lidarSubsystem;
  private static final int SAMPLE_SIZE = 4;
  private final int LIDAR_WAIT_DISTANCE;

  public LidarCommand(int distance) {
    lidarSubsystem = Robot.INJECTOR.lidarSubsystem();
    requires(lidarSubsystem);
    LIDAR_WAIT_DISTANCE = distance;
  }

  @Override
  protected void initialize() {
    logger.debug("lidar init");
  }

  @Override
  protected void execute() {
    int distanceSum = 0;
    for (int i = 0; i < 5; i++) {
      distanceSum += lidarSubsystem.getDistance();
    }
    logger.debug("distance = {}", distanceSum / 4.0);
  }

  public double getDistance() {
    return lidarSubsystem.getDistance();
  }

  @Override
  protected boolean isFinished() {
    return false;
  }

  @Override
  protected void end() {
    lidarSubsystem.end();
    super.end();
  }

  public boolean isInRange() {
    int counter = 0;

    for (int i = 0; i < SAMPLE_SIZE; i++) {
      if (getDistance() < LIDAR_WAIT_DISTANCE) {
        counter++;
      }
    }

    return counter >= SAMPLE_SIZE * 0.75;
  }
}
