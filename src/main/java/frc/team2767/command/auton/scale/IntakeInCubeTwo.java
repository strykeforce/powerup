package frc.team2767.command.auton.scale;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Robot;
import frc.team2767.subsystem.IntakeSubsystem;
import frc.team2767.subsystem.LidarSubsystem;

public class IntakeInCubeTwo extends Command {

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

    System.out.println("Ending IntakeInCubeTwo");
    intakeSubsystem.stop();
  }
}
