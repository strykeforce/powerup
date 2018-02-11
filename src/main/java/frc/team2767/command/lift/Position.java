package frc.team2767.command.lift;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Robot;
import frc.team2767.subsystem.LiftSubsystem;

public class Position extends Command {

  private final Preferences preferences;

  private final LiftSubsystem liftSubsystem;
  private int position;

  public Position() {
    preferences = Preferences.getInstance();
    //    this.position = position;
    liftSubsystem = Robot.INJECTOR.liftSubsystem();
    requires(liftSubsystem);
    preferences.putInt("Lift/7/Position", position);
  }

  @Override
  protected void initialize() {
    position = preferences.getInt("Lift/7/Position", position);
    liftSubsystem.setSetpoint(position);
  }

  @Override
  protected boolean isFinished() {
    return true;
    //    return liftSubsystem.onTarget();
  }
}
