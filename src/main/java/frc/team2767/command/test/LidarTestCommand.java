package frc.team2767.command.test;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Robot;
import frc.team2767.subsystem.LidarSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LidarTestCommand extends Command {
  private static final Logger logger = LoggerFactory.getLogger(LidarTestCommand.class);
  private final LidarSubsystem lidarSubsystem = Robot.INJECTOR.lidarSubsystem();

  public LidarTestCommand() {
    requires(lidarSubsystem);
  }

  @Override
  protected void execute() {
    logger.debug("distance = {}", lidarSubsystem.pidGet());
  }

  @Override
  protected boolean isFinished() {
    return false;
  }
}
