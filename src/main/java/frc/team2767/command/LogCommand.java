package frc.team2767.command;

import edu.wpi.first.wpilibj.command.InstantCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogCommand extends InstantCommand {

  private static final Logger logger = LoggerFactory.getLogger(LogCommand.class);

  private final String message;

  public LogCommand(String message) {
    this.message = message;
    setRunWhenDisabled(true);
  }

  @Override
  protected void initialize() {
    logger.info(message);
  }

  @Override
  public String toString() {
    return "LogCommand{'" + message + "'}";
  }
}
