package frc.team2767.control;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.command.PrintCommand;
import frc.team2767.command.auton.AutonCommandGroup;
import frc.team2767.command.auton.AzimuthCommand;
import frc.team2767.command.climber.ClimbCommand;
import frc.team2767.command.climber.HoldCommand;
import frc.team2767.command.drive.ZeroGyroYawCommand;
import frc.team2767.command.lift.DownCommand;
import frc.team2767.command.lift.StopCommand;
import frc.team2767.command.test.PathCommand;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Accesses driver control input. */
@Singleton
public class Controls {

  private static final Logger logger = LoggerFactory.getLogger(Controls.class);

  private static final int DRIVER_RIGHT_X_AXIS = 0;
  private static final int DRIVER_RIGHT_Y_AXIS = 1;
  private static final int DRIVER_LEFT_Y_AXIS = 2;
  private static final int DRIVER_TUNER_AXIS = 6;
  private static final int DRIVER_LEFT_X_AXIS = 5;

  private static final int DRIVER_LEFT_BUTTON = 1;
  private static final int DRIVER_RIGHT_SHOULDER_BUTTON = 2;
  private static final int DRIVER_RESET_BUTTON = 3;
  private static final int DRIVER_LEFT_SHOULDER_DOWN_BUTTON = 4;
  private static final int DRIVER_LEFT_SHOULDER_UP_BUTTON = 5;

  private static final int DRIVER_LEFT_TRIM_X_POS = 7;
  private static final int DRIVER_LEFT_TRIM_X_NEG = 6;
  private static final int DRIVER_RIGHT_TRIM_X_NEG = 13;
  private static final int DRIVER_RIGHT_TRIM_X_POS = 12;

  private static final int DRIVER_LEFT_TRIM_Y_POS = 8;
  private static final int DRIVER_LEFT_TRIM_Y_NEG = 9;
  private static final int DRIVER_RIGHT_TRIM_Y_POS = 10;
  private static final int DRIVER_RIGHT_RIM_Y_NEG = 11;

  private static final int GAME_A_BUTTON = 1;
  private static final int GAME_B_BUTTON = 2;
  private static final int GAME_X_BUTTON = 3;
  private static final int GAME_Y_BUTTON = 4;
  private static final int GAME_LEFT_SHOULDER_BUTTON = 5;
  private static final int GAME_RIGHT_SHOULDER_BUTTON = 6;
  private static final int GAME_BACK_BUTTON = 7;
  private static final int GAME_START_BUTTON = 8;
  private static final int GAME_LEFT_STICK_BUTTON = 9;
  private static final int GAME_RIGHT_STICK_BUTTON = 10;

  private static final int POWERUP_INTAKE_PORTAL = 0;
  private static final int BOARD_BUTTON_1 = 1;
  private static final int BOARD_BUTTON_2 = 2;
  private static final int BOARD_BUTTON_3 = 3;
  private static final int BOARD_BUTTON_4 = 4;
  private static final int BOARD_BUTTON_5 = 5;
  private static final int BOARD_BUTTON_6 = 6;

  private static final int LEFT = 1;
  private static final int CENTER_LEFT = 2;
  private static final int CENTER_RIGHT = 3;
  private static final int CENTER_RIGHT_EXCHANGE = 4;

  private final Joystick gameController = new Joystick(0);
  private final Joystick driverController = new Joystick(1);
  private final Joystick buttonBoard = new Joystick(3);
  private final Joystick newButtonBoard = new Joystick(3);

  private final Button zeroGyroButton = new JoystickButton(driverController, DRIVER_RESET_BUTTON);
  private final Button button1 = new JoystickButton(buttonBoard, BOARD_BUTTON_1);
  private final Button button2 = new JoystickButton(buttonBoard, BOARD_BUTTON_2);
  private final Button button3 = new JoystickButton(buttonBoard, BOARD_BUTTON_3);
  private final Button button4 = new JoystickButton(buttonBoard, BOARD_BUTTON_4);
  private final Button button5 = new JoystickButton(buttonBoard, BOARD_BUTTON_5);
  private final Button button6 = new JoystickButton(buttonBoard, BOARD_BUTTON_6);

  private final Button autonButton = new JoystickButton(buttonBoard, BOARD_BUTTON_1);
  private final Button testButton = new JoystickButton(buttonBoard, BOARD_BUTTON_2);
  private final Button azimuthTestButton = new JoystickButton(buttonBoard, BOARD_BUTTON_3);
  private final Button liftUpButton = new JoystickButton(gameController, GAME_Y_BUTTON);
  private final Button liftDownButton = new JoystickButton(gameController, GAME_A_BUTTON);

  List<Button> buttons = new ArrayList<>();

  @Inject
  Controls() {
    logger.debug("initializing controls");
    zeroGyroButton.whenPressed(new ZeroGyroYawCommand());
    autonButton.whenPressed(new AutonCommandGroup());
    azimuthTestButton.whenPressed(new AzimuthCommand());
    testButton.whenPressed(new PathCommand(CENTER_RIGHT));
    zeroGyroButton.whenPressed(new ZeroGyroYawCommand());
    liftUpButton.whenPressed(new ClimbCommand());
    liftUpButton.whenReleased(new HoldCommand());
    liftDownButton.whenPressed(new DownCommand());
    liftDownButton.whenReleased(new StopCommand());

    for (int i = 1; i <= 12; i++) {
      Button button = new JoystickButton(newButtonBoard, i);
      button.whenPressed(new PrintCommand("Button " + i));
      buttons.add(button);
    }

    button1.whenPressed(new AutonCommandGroup());
    button2.whenPressed(new AzimuthCommand());
    button3.whenPressed(new PathCommand(LEFT));
    button4.whenPressed(new PathCommand(CENTER_LEFT));
    button5.whenPressed(new PathCommand(CENTER_RIGHT));
    button6.whenPressed(new PathCommand(CENTER_RIGHT_EXCHANGE));
  }

  /**
   * Return the driver controller left stick Y-axis position.
   *
   * @return the position, range is -1.0 (full reverse) to 1.0 (full forward)
   */
  public double getForward() {
    return -driverController.getRawAxis(DRIVER_LEFT_Y_AXIS);
  }

  public boolean getLeftButton() {
    return driverController.getRawButton(DRIVER_LEFT_BUTTON);
  }

  public boolean getRightShoulder() {
    return driverController.getRawButton(DRIVER_RIGHT_SHOULDER_BUTTON);
  }

  public boolean getLeftShoulderUp() {
    return driverController.getRawButton(DRIVER_LEFT_SHOULDER_UP_BUTTON);
  }

  public boolean getLeftShoulderDown() {
    return driverController.getRawButton(DRIVER_LEFT_SHOULDER_DOWN_BUTTON);
  }

  public double getDriverRightY() {
    return -driverController.getRawAxis(DRIVER_RIGHT_Y_AXIS);
  }

  public boolean getDriverLeftTrimXPos() {
    return driverController.getRawButton(DRIVER_LEFT_TRIM_X_POS);
  }

  public boolean getDriverLeftTrimXNeg() {
    return driverController.getRawButton(DRIVER_LEFT_TRIM_X_NEG);
  }

  public boolean getDriverRightTrimXNeg() {
    return driverController.getRawButton(DRIVER_RIGHT_TRIM_X_NEG);
  }

  public boolean getDriverRightTrimXPos() {
    return driverController.getRawButton(DRIVER_RIGHT_TRIM_X_POS);
  }

  public boolean getDriverLeftTrimYPos() {
    return driverController.getRawButton(DRIVER_LEFT_TRIM_Y_POS);
  }

  public boolean getDriverLeftTrimYNeg() {
    return driverController.getRawButton(DRIVER_LEFT_TRIM_Y_NEG);
  }

  public boolean getDriverRightTrimYPos() {
    return driverController.getRawButton(DRIVER_RIGHT_TRIM_Y_POS);
  }

  public boolean getDriverRightTrimYNeg() {
    return driverController.getRawButton(DRIVER_RIGHT_RIM_Y_NEG);
  }

  /**
   * Return the driver controller left stick X-axis position.
   *
   * @return the position, range is -1.0 (full left) to 1.0 (full right)
   */
  public double getStrafe() {
    return driverController.getRawAxis(DRIVER_LEFT_X_AXIS);
  }

  /**
   * Return the driver controller right stick X-axis position.
   *
   * @return the position, range is -1.0 (full left) to 1.0 (full right)
   */
  public double getAzimuth() {
    return driverController.getRawAxis(DRIVER_RIGHT_X_AXIS);
  }

  /**
   * Return the "Ch 6. Flaps Gain" knob value.
   *
   * @return the knob position, range is -1.0 (full left) to 1.0 (full right)
   */
  public double getTuner() {
    return driverController.getRawAxis(DRIVER_TUNER_AXIS);
  }

  /**
   * Return the "Reset" button on flight sim controller.
   *
   * @return true if the button is pressed
   */
  public boolean getResetButton() {
    return driverController.getRawButton(DRIVER_RESET_BUTTON);
  }

  /**
   * Return the gamepad "A" button
   *
   * @return true if the button is pressed
   */
  public boolean getGamepadAButton() {
    return gameController.getRawButton(GAME_A_BUTTON);
  }

  /**
   * Return the gamepad "B" button
   *
   * @return true if the button is pressed
   */
  public boolean getGamepadBButton() {
    return gameController.getRawButton(GAME_B_BUTTON);
  }

  /**
   * Return the gamepad "X" button
   *
   * @return true if the button is pressed
   */
  public boolean getGamepadXButton() {
    return gameController.getRawButton(GAME_X_BUTTON);
  }

  /**
   * Return the gamepad "Y" button
   *
   * @return true if the button is pressed
   */
  public boolean getGamepadYButton() {
    return gameController.getRawButton(GAME_Y_BUTTON);
  }

  /**
   * Return the gamepad "back" button
   *
   * @return true if the button is pressed
   */
  public boolean getGamepadBackButton() {
    return gameController.getRawButton(GAME_BACK_BUTTON);
  }

  /**
   * Return the gamepad "start" button
   *
   * @return true if the button is pressed
   */
  public boolean getGamepadStartButton() {
    return gameController.getRawButton(GAME_START_BUTTON);
  }
}
