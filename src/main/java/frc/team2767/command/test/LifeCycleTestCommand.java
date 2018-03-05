package frc.team2767.command.test;

import edu.wpi.first.wpilibj.command.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LifeCycleTestCommand extends Command {

  static final Logger logger = LoggerFactory.getLogger(LifeCycleTestCommand.class);

  public LifeCycleTestCommand(String name) {
    super(name);
    logger.debug("{}: constructor complete", getName());
  }

  @Override
  protected void initialize() {
    logger.debug("{}: initialize complete", getName());
  }

  @Override
  protected void execute() {
    logger.debug("{}: execute", getName());
  }

  @Override
  protected boolean isFinished() {
    logger.debug("{}: isFinished = true", getName());
    return true;
  }

  @Override
  protected void end() {
    logger.debug("{}: end complete", getName());
  }

  @Override
  protected void interrupted() {
    logger.debug("{}: interrupted complete", getName());
  }
}
