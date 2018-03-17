package frc.team2767.command.intake;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Robot;
import frc.team2767.command.sensors.LidarCommand;
import frc.team2767.subsystem.IntakeSubsystem;

public class IntakeInCubeTwo extends Command {

  private final IntakeSubsystem intakeSubsystem = Robot.INJECTOR.intakeSubsystem();
  private final LidarCommand lidarCommand;

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
    return lidarCommand.isInRange();
  }

  @Override
  protected void end() {

    System.out.println("Ending IntakeInCubeTwo");
    intakeSubsystem.stop();
  }
}
