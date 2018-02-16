package frc.team2767.control;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import frc.team2767.Settings;
import frc.team2767.command.drive.ZeroGyroYawCommand;
import frc.team2767.command.intake.IntakeDriver;
import frc.team2767.command.intake.IntakeEject;
import javax.inject.Inject;
import javax.inject.Singleton;

@SuppressWarnings("unused")
@Singleton
public class DriverControls {

  private final Joystick joystick = new Joystick(1);

  @Inject
  public DriverControls(Settings settings) {
    Controls.logger.debug("initializing driver controls");
    if (settings.isIsolatedTestMode()) return;

    // gyro
    new JoystickButton(joystick, Switch.RESET.index).whenPressed(new ZeroGyroYawCommand());

    // intake
    new JoystickButton(joystick, Shoulder.LEFT_DOWN.index).whenPressed(new IntakeEject());
    new JoystickButton(joystick, Shoulder.RIGHT.index).whenPressed(new IntakeDriver());
  }

  public double getForward() {
    return -joystick.getRawAxis(Axis.LEFT_Y.index);
  }

  public double getStrafe() {
    return joystick.getRawAxis(Axis.LEFT_X.index);
  }

  public double getAzimuth() {
    return joystick.getRawAxis(Axis.RIGHT_X.index);
  }

  public double getTuner() {
    return joystick.getRawAxis(Axis.TUNER.index);
  }

  public double getAxis(Axis axis) {
    return joystick.getRawAxis(axis.index);
  }

  public boolean getSwitch(Switch sw) {
    return joystick.getRawButton(sw.index);
  }

  public boolean getTrim(Trim trim) {
    return joystick.getRawButton(trim.index);
  }

  @SuppressWarnings("unused")
  public enum Shoulder {
    RIGHT(2),
    LEFT_DOWN(4),
    LEFT_UP(5);

    private final int index;

    Shoulder(int index) {
      this.index = index;
    }
  }

  @SuppressWarnings("unused")
  public enum Switch {
    LEFT(1),
    RESET(3);

    private final int index;

    Switch(int index) {
      this.index = index;
    }
  }

  @SuppressWarnings("unused")
  public enum Trim {
    LEFT_X_POS(7),
    LEFT_X_NEG(6),
    LEFT_Y_POS(8),
    LEFT_Y_NEG(9),
    RIGHT_Y_POS(10),
    RIGHT_Y_NEG(11),
    RIGHT_X_POS(12),
    RIGHT_X_NEG(13);

    private final int index;

    Trim(int index) {
      this.index = index;
    }
  }

  @SuppressWarnings("unused")
  public enum Axis {
    RIGHT_X(0),
    RIGHT_Y(1),
    LEFT_X(5),
    LEFT_Y(2),
    TUNER(6);

    private final int index;

    Axis(int index) {
      this.index = index;
    }
  }
}
