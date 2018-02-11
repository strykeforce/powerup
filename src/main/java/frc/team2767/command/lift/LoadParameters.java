package frc.team2767.command.lift;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.Robot;
import frc.team2767.subsystem.LiftSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoadParameters extends InstantCommand {

  private static final Logger logger = LoggerFactory.getLogger(LoadParameters.class);

  private final LiftSubsystem liftSubsystem;

  public LoadParameters() {
    liftSubsystem = Robot.INJECTOR.liftSubsystem();
    requires(liftSubsystem);
  }

  @Override
  protected void initialize() {
    liftSubsystem.loadParameters();
    logger.info("Command invoked");
  }
}
