package frc.team2767.command.auton.scale;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Robot;
import frc.team2767.subsystem.IntakeSubsystem;
import frc.team2767.subsystem.LidarSubsystem;
import org.slf4j.Logger;

public class IntakeInCubeTwo extends Command {

  static final Logger logger = ScaleCommandGroup.logger;

  private final IntakeSubsystem intakeSubsystem = Robot.INJECTOR.intakeSubsystem();
  private final LidarSubsystem lidarSubsystem = Robot.INJECTOR.lidarSubsystem();
  private final int distance;

  public IntakeInCubeTwo(int distance) {
    this.distance = distance;
    requires(lidarSubsystem);
    requires(intakeSubsystem);
  }

  @Override
  protected void initialize() {
    intakeSubsystem.run(IntakeSubsystem.Mode.LOAD);
  }

  @Override
  protected boolean isFinished() {
    return lidarSubsystem.isInRange(distance);
  }

  @Override
  protected void end() {
    intakeSubsystem.stop();
    logger.trace("IntakeInCubeTwo ENDED");
  }
}
