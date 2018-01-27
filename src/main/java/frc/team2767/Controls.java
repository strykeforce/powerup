package frc.team2767;

import static edu.wpi.first.wpilibj.DriverStation.kJoystickPorts;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import frc.team2767.command.ZeroGyroYawCommand;
import frc.team2767.command.auton.AutonCommandGroup;
import frc.team2767.command.auton.AzimuthCommand;
import frc.team2767.subsystem.DriveSubsystem;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Accesses driver control input. */
@Singleton
public class Controls {

  private static final Logger logger = LoggerFactory.getLogger(DriveSubsystem.class);
  private static final DriverStation driverStation = DriverStation.getInstance();

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

  private static final int BOARD_BUTTON_1 = 1;
  private static final int BOARD_BUTTON_2 = 2;
  private static final int BOARD_BUTTON_3 = 3;

  // not sure of controller names, temp placeholders
  private static final String INTERLINK_X_DRIVER_CONTROLLER = "Interlink-X";
  private static final String XBOX_CONTROLLER_GAME_CONTROLLER = "X-Box";
  private static final String BUTTON_BOARD = "Unidentified Controller";

  private final Joystick gameController =
      new Joystick(getControllerPort(INTERLINK_X_DRIVER_CONTROLLER));
  private final Joystick driverController =
      new Joystick(getControllerPort(XBOX_CONTROLLER_GAME_CONTROLLER));
  private final Joystick buttonBoard = new Joystick(getControllerPort(BUTTON_BOARD));

  private final Button zeroGyroButton = new JoystickButton(driverController, DRIVER_RESET_BUTTON);
  private final Button autonButton = new JoystickButton(buttonBoard, BOARD_BUTTON_1);
  private final Button closedLoopTestButton = new JoystickButton(buttonBoard, BOARD_BUTTON_2);
  private final Button azimuthTestButton = new JoystickButton(buttonBoard, BOARD_BUTTON_3);

  @Inject
  public Controls() {
    zeroGyroButton.whenPressed(new ZeroGyroYawCommand());
    autonButton.whenPressed(new AutonCommandGroup());
    azimuthTestButton.whenPressed(new AzimuthCommand());
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

  /**
   * Identifies USB port of specified controller
   *
   * @return the port number
   */
  private int getControllerPort(String name) {
    for (int i = 0; i < kJoystickPorts; i++) {
      if (name.equals(driverStation.getJoystickName(i))) {
        return i;
      }
    }

    return 0;
  }
}
