package frc.team2767.command.auton.scale;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Robot;
import frc.team2767.command.sensors.LidarCommand;
import frc.team2767.subsystem.IntakeSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntakeInCubeTwo extends Command {

  private final IntakeSubsystem intakeSubsystem = Robot.INJECTOR.intakeSubsystem();
  private final LidarCommand lidarCommand;

  private static final Logger logger = LoggerFactory.getLogger(IntakeInCubeTwo.class);

  public IntakeInCubeTwo(int distance) {
    requires(intakeSubsystem);
    lidarCommand = new LidarCommand(distance);
  }

  @Override
  protected void initialize() {
    intakeSubsystem.run(IntakeSubsystem.Mode.LOAD);
  }

  @Override
  protected boolean isFinished() {
    logger.debug("{} {}", lidarCommand.isInRange(), lidarCommand.getDistance());
    return lidarCommand.isInRange(); // FIXME: can call subsystem
  }

  @Override
  protected void end() {

    System.out.println("Ending IntakeInCubeTwo");
    intakeSubsystem.stop();
  }
}
