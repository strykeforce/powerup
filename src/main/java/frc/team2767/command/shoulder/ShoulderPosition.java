package frc.team2767.command.shoulder;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Robot;
import frc.team2767.Settings;
import frc.team2767.subsystem.ShoulderSubsystem;

public class ShoulderPosition extends Command {

  private final ShoulderSubsystem shoulderSubsystem = Robot.INJECTOR.shoulderSubsystem();
  private final int position;

  public ShoulderPosition(Position position) {
    Settings settings = Robot.INJECTOR.settings();
    this.position = position.positionFrom(settings);
    requires(shoulderSubsystem);
  }

  @Override
  protected void initialize() {
    shoulderSubsystem.setPosition(position);
  }

  @Override
  protected boolean isFinished() {
    return shoulderSubsystem.onTarget();
  }

  public enum Position {
    ZERO("limitSwitchZeroPosition"),
    TIGHT_STOW("tightStowPosition"),
    STOW("stowPosition"),
    LAUNCH_SWITCH("launchSwitchPosition"),
    LAUNCH_SCALE("launchScalePosition"),
    INTAKE("intakePosition");

    private final String key;

    Position(String key) {
      this.key = key;
    }

    public int positionFrom(Settings settings) {
      return settings.getTable("POWERUP.SHOULDER").getLong(key).intValue();
    }
  }
}
