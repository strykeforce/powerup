package frc.team2767.command;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.Robot;
import frc.team2767.subsystem.DriveSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlignWheelsCommand extends InstantCommand {

  private static final Logger logger = LoggerFactory.getLogger(AlignWheelsCommand.class);

  private final DriveSubsystem driveSubsystem;

  public AlignWheelsCommand() {
    driveSubsystem = Robot.COMPONENT.driveSubsystem();
    requires(driveSubsystem);
    logger.debug("constructor");
  }

  @Override
  protected void initialize() {
    driveSubsystem.alignWheels();
    logger.debug("initialized");
  }
}
