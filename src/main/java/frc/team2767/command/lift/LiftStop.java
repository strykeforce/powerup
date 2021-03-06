package frc.team2767.command.lift;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.Robot;
import frc.team2767.subsystem.LiftSubsystem;

public class LiftStop extends InstantCommand {

  private final LiftSubsystem liftSubsystem = Robot.INJECTOR.liftSubsystem();;

  public LiftStop() {
    super("Lift Stop");
    requires(liftSubsystem);
  }

  @Override
  protected void initialize() {
    liftSubsystem.stop();
  }
}
