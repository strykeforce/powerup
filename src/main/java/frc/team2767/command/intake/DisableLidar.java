package frc.team2767.command.intake;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.Robot;
import frc.team2767.subsystem.IntakeSensorsSubsystem;

public class DisableLidar extends InstantCommand {

  private final IntakeSensorsSubsystem intakeSensorsSubsystem =
      Robot.INJECTOR.intakeSensorsSubsystem();

  public DisableLidar() {
    requires(intakeSensorsSubsystem);
  }

  @Override
  protected void initialize() {
    intakeSensorsSubsystem.enableLidar(false);
  }
}
