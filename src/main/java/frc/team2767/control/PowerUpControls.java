package frc.team2767.control;

import com.moandjiezana.toml.Toml;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.buttons.Trigger;
import frc.team2767.Settings;
import frc.team2767.command.LogCommand;
import frc.team2767.command.intake.IntakeIn;
import frc.team2767.command.intake.IntakeOut;
import frc.team2767.command.intake.IntakeStop;
import frc.team2767.command.lift.LiftDown;
import frc.team2767.command.lift.LiftPosition;
import frc.team2767.command.lift.LiftUp;
import frc.team2767.command.sequence.Stow;
import frc.team2767.command.shoulder.ShoulderDown;
import frc.team2767.command.shoulder.ShoulderPosition;
import frc.team2767.command.shoulder.ShoulderUp;
import frc.team2767.command.shoulder.ShoulderZero;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PowerUpControls {

  private final Joystick board = new Joystick(0);

  @Inject
  public PowerUpControls(Settings settings) {
    Controls.logger.debug("initializing POWER UP button board controls");
    if (settings.isIsolatedTestMode()) return;

    Toml toml = settings.getTable("POWERUP.LIFT");
    int kScaleLow = toml.getLong("scaleLowPosition").intValue();
    int kScaleMid = toml.getLong("scaleMidPosition").intValue();
    int kScaleHigh = toml.getLong("scaleHighPosition").intValue();
    toml = settings.getTable("POWERUP.SHOULDER");
    int kIntakePosition = toml.getLong("intakePosition").intValue();

    // intake
    Button intakeIn = new JoystickButton(board, Switch.INTAKE_IN.index);
    intakeIn.whenActive(new IntakeIn());
    intakeIn.whenReleased(new IntakeStop());

    Button intakeOut = new JoystickButton(board, Switch.INTAKE_OUT.index);
    intakeOut.whenActive(new IntakeOut());
    intakeOut.whenReleased(new IntakeStop());

    // climber
    new JoystickButton(board, Switch.CLIMB.index).whenActive(new LogCommand("climb"));
    new JoystickButton(board, Switch.CLIMBER_TRANSFORM.index)
        .whenActive(new LogCommand("transformers!!!!!!"));

    // lift
    new JoystickButton(board, Switch.LIFT_UP.index).whileActive(new LiftUp());
    new JoystickButton(board, Switch.LIFT_DOWN.index).whileActive(new LiftDown());

    new Trigger() {
      @Override
      public boolean get() {
        return board.getRawAxis(Axis.STOW.index) > 0.9;
      }
    }.whenActive(new Stow());

    new Trigger() {
      @Override
      public boolean get() {
        return board.getRawAxis(Axis.LIFT_LOW_SCALE.index) < -0.9;
      }
    }.whenActive(new LiftPosition(kScaleLow));

    new Trigger() {
      @Override
      public boolean get() {
        return Math.abs(board.getRawAxis(Axis.LIFT_HIGH_SCALE.index)) > 0.9;
      }
    }.whenActive(new LiftPosition(kScaleHigh));

    new JoystickButton(board, Switch.LIFT_MID_SCALE.index).whenActive(new LiftPosition(kScaleMid));
    new JoystickButton(board, Switch.EXCHANGE_POS.index).whenActive(new ShoulderZero());
    new JoystickButton(board, Switch.PORTAL_INTAKE.index)
        .whenActive(new LogCommand("portal intake button"));
    new JoystickButton(board, Switch.GROUND_INTAKE_POS.index)
        .whenActive(new ShoulderPosition(kIntakePosition));

    // shoulder
    new JoystickButton(board, Switch.SHOULDER_UP.index).whileActive(new ShoulderUp());
    new JoystickButton(board, Switch.SHOULDER_DOWN.index).whileActive(new ShoulderDown());

    Controls.logger.info("scaleLowPosition = {}", kScaleLow);
    Controls.logger.info("scaleMidPosition = {}", kScaleMid);
    Controls.logger.info("scaleHighPosition = {}", kScaleHigh);
    Controls.logger.info("intakePosition = {}", kIntakePosition);
  }

  public enum Axis {
    STOW(0),
    LIFT_LOW_SCALE(0),
    LIFT_HIGH_SCALE(1);
    private final int index;

    Axis(int index) {
      this.index = index;
    }
  }

  public enum Switch {
    SHOULDER_UP(4),
    SHOULDER_DOWN(3),
    LIFT_UP(2),
    LIFT_DOWN(1),
    CLIMB(5),
    CLIMBER_TRANSFORM(6),
    LIFT_MID_SCALE(7),
    PORTAL_INTAKE(8),
    INTAKE_IN(11),
    INTAKE_OUT(9),
    GROUND_INTAKE_POS(10),
    EXCHANGE_POS(12);
    private final int index;

    Switch(int index) {
      this.index = index;
    }
  }
}
