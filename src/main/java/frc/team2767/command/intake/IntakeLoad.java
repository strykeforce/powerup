package frc.team2767.command.intake;

import static frc.team2767.subsystem.IntakeSubsystem.Mode.LOAD;

import com.moandjiezana.toml.Toml;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.Robot;
import frc.team2767.Settings;
import frc.team2767.subsystem.IntakeSubsystem;
import frc.team2767.subsystem.LiftSubsystem;
import frc.team2767.subsystem.ShoulderSubsystem;

public class IntakeLoad extends CommandGroup {

  public IntakeLoad(Position position) {
    addParallel(new Intake());
    addParallel(new PositionLift(position));
    addSequential(new DropShoulder());
  }

  static class Intake extends Command {

    private final IntakeSubsystem intakeSubsystem = Robot.INJECTOR.intakeSubsystem();

    public Intake() {
      requires(intakeSubsystem);
    }

    @Override
    protected void initialize() {
      intakeSubsystem.run(LOAD);
    }

    @Override
    protected boolean isFinished() {
      return true;
    }
  }

  // lower lift to lowest position
  static class PositionLift extends Command {

    private final LiftSubsystem liftSubsystem = Robot.INJECTOR.liftSubsystem();
    private final int position;

    PositionLift(Position position) {
      Settings settings = Robot.INJECTOR.settings();
      Toml toml = settings.getTable("POWERUP.LIFT");
      int kLiftPortal = toml.getLong("portalPosition").intValue();
      int kLiftStow = toml.getLong("stowPosition").intValue();

      switch (position) {
        case GROUND:
          this.position = kLiftStow;
          break;
        case PORTAL:
          this.position = kLiftPortal;
          break;
        default:
          this.position = kLiftStow;
      }
      requires(liftSubsystem);
    }

    @Override
    protected void initialize() {
      liftSubsystem.setPosition(position);
    }

    @Override
    protected boolean isFinished() {
      return liftSubsystem.onTarget();
    }
  }

  // lower shoulder to position used when loading cube
  static class DropShoulder extends Command {

    private final ShoulderSubsystem shoulderSubsystem = Robot.INJECTOR.shoulderSubsystem();
    private final int position;

    DropShoulder() {
      position =
          Robot.INJECTOR
              .settings()
              .getTable("POWERUP.SHOULDER")
              .getLong("intakePosition")
              .intValue();
      requires(shoulderSubsystem);
    }

    @Override
    protected void initialize() {
      shoulderSubsystem.setPosition(position);
    }

    @Override
    protected boolean isFinished() {
      return true;
    }
  }

  public enum Position {
    PORTAL,
    GROUND
  }
}
