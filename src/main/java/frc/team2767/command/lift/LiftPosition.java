package frc.team2767.command.lift;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Robot;
import frc.team2767.Settings;
import frc.team2767.subsystem.LiftSubsystem;

public class LiftPosition extends Command {

  private final LiftSubsystem liftSubsystem = Robot.INJECTOR.liftSubsystem();
  private final int position;

  public LiftPosition(Position position) {
    Settings settings = Robot.INJECTOR.settings();
    this.position = position.positionFrom(settings);
    requires(liftSubsystem);
  }

  @Override
  protected void initialize() {
    liftSubsystem.setPosition(position);
  }

  @Override
  protected void execute() {
    liftSubsystem.adjustVelocity();
  }

  @Override
  protected boolean isFinished() {
    return liftSubsystem.onTarget();
  }

  public enum Position {
    SCALE_LOW("scaleLowPosition"),
    SCALE_MID("scaleMidPosition"),
    SCALE_HIGH("scaleHighPosition"),
    STOW("stowPosition"),
    PORTAL("portalPosition"),
    ;
    private final String key;

    Position(String key) {
      this.key = key;
    }

    public int positionFrom(Settings settings) {
      assert (settings != null);
      return settings.getTable("POWERUP.LIFT").getLong(key).intValue();
    }
  }
}
