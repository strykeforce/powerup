package frc.team2767.control;

import edu.wpi.first.wpilibj.Joystick;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class GamepadControls {

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

  private final Joystick gamepad = new Joystick(2);

  @Inject
  public GamepadControls() {}

  /**
   * Return the gamepad "A" button
   *
   * @return true if the button is pressed
   */
  public boolean getGamepadAButton() {
    return gamepad.getRawButton(GAME_A_BUTTON);
  }

  /**
   * Return the gamepad "B" button
   *
   * @return true if the button is pressed
   */
  public boolean getGamepadBButton() {
    return gamepad.getRawButton(GAME_B_BUTTON);
  }

  /**
   * Return the gamepad "X" button
   *
   * @return true if the button is pressed
   */
  public boolean getGamepadXButton() {
    return gamepad.getRawButton(GAME_X_BUTTON);
  }

  /**
   * Return the gamepad "Y" button
   *
   * @return true if the button is pressed
   */
  public boolean getGamepadYButton() {
    return gamepad.getRawButton(GAME_Y_BUTTON);
  }

  /**
   * Return the gamepad "back" button
   *
   * @return true if the button is pressed
   */
  public boolean getGamepadBackButton() {
    return gamepad.getRawButton(GAME_BACK_BUTTON);
  }

  /**
   * Return the gamepad "start" button
   *
   * @return true if the button is pressed
   */
  public boolean getGamepadStartButton() {
    return gamepad.getRawButton(GAME_START_BUTTON);
  }
}
