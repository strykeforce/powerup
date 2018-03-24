package frc.team2767.command.auton.scale;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Robot;
import frc.team2767.subsystem.IntakeSensorsSubsystem;
import frc.team2767.subsystem.IntakeSubsystem;
import org.slf4j.Logger;

public class IntakeInCubeTwo extends Command {

  static final Logger logger = ScaleCommandGroup.logger;

  private final IntakeSubsystem intakeSubsystem = Robot.INJECTOR.intakeSubsystem();
  private final IntakeSensorsSubsystem intakeSensorsSubsystem =
      Robot.INJECTOR.intakeSensorsSubsystem();
  private final int distance;

  public IntakeInCubeTwo(int distance) {
    this.distance = distance;
    requires(intakeSubsystem);
  }

  @Override
  protected void initialize() {
    intakeSubsystem.run(IntakeSubsystem.Mode.LOAD);
    logger.info("intake running, lidar distance = {}", intakeSensorsSubsystem.getLidarDistance());
  }

  @Override
  protected boolean isFinished() {
    return intakeSensorsSubsystem.isLidarDisanceWithin(distance);
  }

  @Override
  protected void end() {
    intakeSubsystem.stop();
    logger.info("intake stopped, lidar distance = {}", intakeSensorsSubsystem.getLidarDistance());
    logger.trace("IntakeInCubeTwo ENDED");
  }
}
