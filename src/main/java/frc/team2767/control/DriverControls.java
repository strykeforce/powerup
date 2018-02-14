package frc.team2767.control;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import frc.team2767.command.drive.ZeroGyroYawCommand;
import frc.team2767.command.intake.IntakeEject;
import frc.team2767.command.intake.IntakeLoad;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DriverControls {

  private final Joystick joystick = new Joystick(1);

  @Inject
  public DriverControls() {
    // gyro
    new JoystickButton(joystick, Switch.RESET.index).whenPressed(new ZeroGyroYawCommand());

    // intake
    new JoystickButton(joystick, Shoulder.LEFT_DOWN.index).whenPressed(new IntakeEject());
    new JoystickButton(joystick, Shoulder.RIGHT.index).whenPressed(new IntakeLoad());
  }

  /**
   * Return the driver controller left stick Y-axis position.
   *
   * @return the position, range is -1.0 (full reverse) to 1.0 (full forward)
   */
  public double getForward() {
    return -joystick.getRawAxis(Axis.LEFT_Y.index);
  }

  /**
   * Return the driver controller left stick X-axis position.
   *
   * @return the position, range is -1.0 (full left) to 1.0 (full right)
   */
  public double getStrafe() {
    return joystick.getRawAxis(Axis.LEFT_X.index);
  }

  /**
   * Return the driver controller right stick X-axis position.
   *
   * @return the position, range is -1.0 (full left) to 1.0 (full right)
   */
  public double getAzimuth() {
    return joystick.getRawAxis(Axis.RIGHT_X.index);
  }

  public double getDriverRightY() {
    return -joystick.getRawAxis(Axis.RIGHT_Y.index);
  }

  /**
   * Return the "Ch 6. Flaps Gain" knob value.
   *
   * @return the knob position, range is -1.0 (full left) to 1.0 (full right)
   */
  public double getTuner() {
    return joystick.getRawAxis(Axis.TUNER.index);
  }

  /**
   * Return the "Reset" button on flight sim controller.
   *
   * @return true if the button is pressed
   */
  public boolean getResetButton() {
    return joystick.getRawButton(Switch.RESET.index);
  }

  public boolean getLeftButton() {
    return joystick.getRawButton(Switch.LEFT.index);
  }

  public boolean getRightShoulder() {
    return joystick.getRawButton(Shoulder.RIGHT.index);
  }

  public boolean getLeftShoulderUp() {
    return joystick.getRawButton(Shoulder.LEFT_UP.index);
  }

  public boolean getLeftShoulderDown() {
    return joystick.getRawButton(Shoulder.LEFT_DOWN.index);
  }

  public boolean getDriverLeftTrimXPos() {
    return joystick.getRawButton(Switch.LEFT_TRIM_X_POS.index);
  }

  public boolean getDriverLeftTrimXNeg() {
    return joystick.getRawButton(Switch.LEFT_TRIM_X_NEG.index);
  }

  public boolean getDriverRightTrimXNeg() {
    return joystick.getRawButton(Switch.RIGHT_TRIM_X_NEG.index);
  }

  public boolean getDriverRightTrimXPos() {
    return joystick.getRawButton(Switch.RIGHT_TRIM_X_POS.index);
  }

  public boolean getDriverLeftTrimYPos() {
    return joystick.getRawButton(Switch.LEFT_TRIM_Y_POS.index);
  }

  public boolean getDriverLeftTrimYNeg() {
    return joystick.getRawButton(Switch.LEFT_TRIM_Y_NEG.index);
  }

  public boolean getDriverRightTrimYPos() {
    return joystick.getRawButton(Switch.RIGHT_TRIM_Y_POS.index);
  }

  public boolean getDriverRightTrimYNeg() {
    return joystick.getRawButton(Switch.RIGHT_TRIM_Y_NEG.index);
  }

  private enum Shoulder {
    RIGHT(2),
    LEFT_DOWN(4),
    LEFT_UP(5),
    ;

    private final int index;

    Shoulder(int index) {
      this.index = index;
    }
  }

  private enum Switch {
    LEFT(1),
    RESET(3),
    LEFT_TRIM_X_POS(7),
    LEFT_TRIM_X_NEG(6),
    LEFT_TRIM_Y_POS(8),
    LEFT_TRIM_Y_NEG(9),
    RIGHT_TRIM_Y_POS(10),
    RIGHT_TRIM_Y_NEG(11),
    RIGHT_TRIM_X_POS(12),
    RIGHT_TRIM_X_NEG(13),
    ;
    private final int index;

    Switch(int index) {
      this.index = index;
    }
  }

  private enum Axis {
    RIGHT_X(0),
    RIGHT_Y(1),
    LEFT_X(5),
    LEFT_Y(2),
    TUNER(6),
    ;
    private final int index;

    Axis(int index) {
      this.index = index;
    }
  }
}
