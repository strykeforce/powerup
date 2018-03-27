package frc.team2767.command.test;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Robot;
import frc.team2767.subsystem.IntakeSensorsSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LidarTestCommand extends Command {
  private static final Logger logger = LoggerFactory.getLogger(LidarTestCommand.class);
  private final IntakeSensorsSubsystem intakeSensorsSubsystem =
      Robot.INJECTOR.intakeSensorsSubsystem();

  public LidarTestCommand() {
    requires(intakeSensorsSubsystem);
  }

  @Override
  protected void initialize() {
    intakeSensorsSubsystem.enableLidar(true);
  }

  @Override
  protected void execute() {
    logger.debug("distance = {}", intakeSensorsSubsystem.getLidarDistance());
  }

  @Override
  protected boolean isFinished() {
    return false;
  }

  @Override
  protected void end() {
    intakeSensorsSubsystem.enableLidar(false);
  }
}
