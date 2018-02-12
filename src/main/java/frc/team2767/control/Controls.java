package frc.team2767.control;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.buttons.Trigger;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2767.Settings;
import frc.team2767.command.LogCommand;
import frc.team2767.command.drive.ZeroGyroYawCommand;
import frc.team2767.command.intake.IntakeEject;
import frc.team2767.command.intake.IntakeIn;
import frc.team2767.command.intake.IntakeLoad;
import frc.team2767.command.intake.IntakeOut;
import frc.team2767.command.intake.IntakeStop;
import frc.team2767.command.lift.SaveZero;
import frc.team2767.command.lift.Zero;
import frc.team2767.command.sequence.Stow;
import frc.team2767.command.shoulder.LoadParameters;
import frc.team2767.command.shoulder.ShoulderDown;
import frc.team2767.command.shoulder.ShoulderOpenLoopDown;
import frc.team2767.command.shoulder.ShoulderOpenLoopUp;
import frc.team2767.command.shoulder.ShoulderPosition;
import frc.team2767.command.shoulder.ShoulderStop;
import frc.team2767.command.shoulder.ShoulderUp;
import frc.team2767.command.shoulder.ShoulderZero;
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

  private static final int POWERUP_SHOULDER_DOWN = 3;
  private static final int POWERUP_SHOULDER_UP = 4;
  private static final int POWERUP_LIFT_DOWN = 1;
  private static final int POWERUP_LIFT_UP = 2;
  private static final int POWERUP_CLIMB_BUTTON = 5;
  private static final int POWERUP_TRANSFORM = 6;
  private static final int POWERUP_MID_SCALE = 7;
  private static final int POWERUP_PORTAL_INTAKE = 8;
  private static final int POWERUP_INTAKE_OUT = 9;
  private static final int POWERUP_GROUND_INTAKE_POS = 10;
  private static final int POWERUP_INTAKE_IN = 11;
  private static final int POWERUP_EXCHANGE_POS = 12;

  private static final int POWERUP_BOARD_BUTTON_Y_AXIS = 1;
  private static final int POWERUP_BOARD_BUTTON_X_AXIS = 0;

  private static final int LEFT = 1;
  private static final int CENTER_LEFT = 2;
  private static final int CENTER_RIGHT = 3;
  private static final int CENTER_RIGHT_EXCHANGE = 4;

  private final List<DigitalInput> digitalInputs = new ArrayList<>();

  private final Joystick powerupButtonBoard = new Joystick(0);
  private final Joystick driverController = new Joystick(1);

  //
  //  Button Board
  //

  private final Button powerupShoulderDownButton =
      new JoystickButton(powerupButtonBoard, POWERUP_SHOULDER_DOWN);
  private final Button powerupShoulderUpButton =
      new JoystickButton(powerupButtonBoard, POWERUP_SHOULDER_UP);
  private final Button powerupElevatorDownButton =
      new JoystickButton(powerupButtonBoard, POWERUP_LIFT_DOWN);
  private final Button powerupElevatorUpButton =
      new JoystickButton(powerupButtonBoard, POWERUP_LIFT_UP);
  private final Button powerupClimbButton =
      new JoystickButton(powerupButtonBoard, POWERUP_CLIMB_BUTTON);
  private final Trigger powerupLowScaleButton;
  private final Trigger powerupScaleHighButton;
  private final Button powerupScaleMidButton =
      new JoystickButton(powerupButtonBoard, POWERUP_MID_SCALE);
  private final Button powerupPortalIntakeButton =
      new JoystickButton(powerupButtonBoard, POWERUP_PORTAL_INTAKE);
  private final Button powerupIntakeOutButton =
      new JoystickButton(powerupButtonBoard, POWERUP_INTAKE_OUT);
  private final Button powerupGroundIntakePosButton =
      new JoystickButton(powerupButtonBoard, POWERUP_GROUND_INTAKE_POS);
  private final Button powerupIntakeInButton =
      new JoystickButton(powerupButtonBoard, POWERUP_INTAKE_IN);
  private final Trigger powerupStowButton;
  private final Button powerupExchangePosButton =
      new JoystickButton(powerupButtonBoard, POWERUP_EXCHANGE_POS);
  private final Button powerupTransformerButton =
      new JoystickButton(powerupButtonBoard, POWERUP_TRANSFORM);

  //
  // Flight Sim
  //
  private final Button driverIntakeEject =
      new JoystickButton(driverController, DRIVER_LEFT_SHOULDER_DOWN_BUTTON);
  private final Button driverIntakeLoad =
      new JoystickButton(driverController, DRIVER_RIGHT_SHOULDER_BUTTON);

  private final Button zeroGyroButton = new JoystickButton(driverController, DRIVER_RESET_BUTTON);
  //  private final Button button1 = new JoystickButton(buttonBoard, BOARD_BUTTON_1);
  //  private final Button button2 = new JoystickButton(buttonBoard, BOARD_BUTTON_2);
  //  private final Button button3 = new JoystickButton(buttonBoard, BOARD_BUTTON_3);
  //  private final Button button4 = new JoystickButton(buttonBoard, BOARD_BUTTON_4);
  //  private final Button button5 = new JoystickButton(buttonBoard, BOARD_BUTTON_5);
  //  private final Button button6 = new JoystickButton(buttonBoard, BOARD_BUTTON_6);
  //
  //  private final Button autonButton = new JoystickButton(buttonBoard, BOARD_BUTTON_1);
  //  private final Button testButton = new JoystickButton(buttonBoard, BOARD_BUTTON_2);
  //  private final Button azimuthTestButton = new JoystickButton(buttonBoard, BOARD_BUTTON_3);
  //  private final Button flipper =
  //      new JoystickButton(driverController, DRIVER_LEFT_SHOULDER_UP_BUTTON);

  //  private final Button autonButton = new JoystickButton(buttonBoard, BOARD_BUTTON_1);
  //  private final Button testButton = new JoystickButton(buttonBoard, BOARD_BUTTON_2);
  //  private final Button azimuthTestButton = new JoystickButton(buttonBoard, BOARD_BUTTON_3);
  //  private final Button liftUpButton = new JoystickButton(gameController, GAME_Y_BUTTON);
  //  private final Button liftDownButton = new JoystickButton(gameController, GAME_A_BUTTON);

  List<Button> buttons = new ArrayList<>();

  @Inject
  Controls(Settings settings) {
    for (int i = 0; i < 6; i++) {
      digitalInputs.add(i, new DigitalInput(i));
    }

    assignDriverButtons();
    assignSmartDashboardButtons();

    powerupScaleHighButton = new ButtonBoardAxisTriggerYNeg(this);
    powerupScaleHighButton.whenActive(new LogCommand("Button powerup scale high is active"));
    powerupScaleMidButton.whenActive(new LogCommand("scale mid"));

    powerupStowButton = new ButtonBoardAxisTriggerXPos(this);
    powerupStowButton.whenActive(new Stow());

    powerupLowScaleButton = new ButtonBoardAxisTriggerXNeg(this);
    powerupLowScaleButton.whenActive(new LogCommand("Low scale"));

    powerupElevatorUpButton.whenActive(new LogCommand("elevator up"));
    powerupElevatorDownButton.whenActive(new LogCommand("elevator down"));

    powerupShoulderUpButton.whileActive(new ShoulderUp());
    powerupShoulderDownButton.whileActive(new ShoulderDown());

    powerupExchangePosButton.whenActive(new ShoulderPosition(0));

    powerupPortalIntakeButton.whenActive(new LogCommand("portal intake button"));

    powerupGroundIntakePosButton.whenActive(new ShoulderPosition(0));

    powerupIntakeInButton.whenActive(new IntakeIn());
    powerupIntakeInButton.whenReleased(new IntakeStop());
    powerupIntakeOutButton.whenActive(new IntakeOut());
    powerupIntakeOutButton.whenReleased(new IntakeStop());

    powerupClimbButton.whenActive(new LogCommand("climb"));

    powerupTransformerButton.whenActive(new LogCommand("transformers!!!!!!"));

    if (settings.isIsolatedTestMode()) {
      logger.info("initializing controls in isolated test mode");
      return;
    }

    logger.info("initializing robot controls");
    //    flipper.whenPressed(new FlipperLaunchCommand());
    zeroGyroButton.whenPressed(new ZeroGyroYawCommand());
    //    liftUpButton.whenPressed(new ClimbCommand());
    //    liftUpButton.whenReleased(new HoldCommand());
    //    button3.whenPressed(new PathCommand(LEFT));
    //    button4.whenPressed(new PathCommand(CENTER_LEFT));
    //    button5.whenPressed(new PathCommand(CENTER_RIGHT));
    //    button6.whenPressed(new PathCommand(CENTER_RIGHT_EXCHANGE));

  }

  /**
   * Read the selected autonomous mode from the binary-code hexadecimal switch. Don't be fooled by
   * hex numbers when debugging, for example switch position 24 (hex) = 36 (dec).
   *
   * <p>The switch wiring labelled 0-5 are connected to corresponding DIO ports 0-5.
   *
   * @return the switch position
   */
  public int getAutonomousSwitchPosition() {
    int val = 0;
    for (int i = 6; i-- > 0; ) {
      val = val << 1;
      val = (val & 0xFE) | (digitalInputs.get(i).get() ? 0 : 1);
    }
    return val;
  }

  private void assignDriverButtons() {
    driverIntakeEject.whenPressed(new IntakeEject());
    driverIntakeLoad.whileActive(new IntakeLoad());
  }

  private void assignSmartDashboardButtons() {
    SmartDashboard.putData("Intake/Load", new IntakeLoad());
    SmartDashboard.putData("Intake/Eject", new IntakeEject());

    SmartDashboard.putData("Shoulder/LoadParametersCommand", new LoadParameters());
    SmartDashboard.putData("Shoulder/PositionCommand", new ShoulderPosition(6000));
    SmartDashboard.putData("Shoulder/Zero", new ShoulderZero());
    SmartDashboard.putData("Lift/SaveZero", new SaveZero());
    SmartDashboard.putData("Lift/Zero", new Zero());

    SmartDashboard.putData("Shoulder/Up", new ShoulderOpenLoopUp());
    SmartDashboard.putData("Shoulder/Down", new ShoulderOpenLoopDown());
    SmartDashboard.putData("Shoulder/Stop", new ShoulderStop());
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

  public double getButtonBoardYNeg() {
    return powerupButtonBoard.getRawAxis(POWERUP_BOARD_BUTTON_Y_AXIS);
  }

  public double getButtonBoardXNeg() {
    return powerupButtonBoard.getRawAxis(POWERUP_BOARD_BUTTON_X_AXIS);
  }

  public double getButtonBoardXPos() {
    return powerupButtonBoard.getRawAxis(POWERUP_BOARD_BUTTON_X_AXIS);
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
  //  public boolean getGamepadAButton() {
  //    return gameController.getRawButton(GAME_A_BUTTON);
  //  }

  /**
   * Return the gamepad "B" button
   *
   * @return true if the button is pressed
   */
  //  public boolean getGamepadBButton() {
  //    return gameController.getRawButton(GAME_B_BUTTON);
  //  }

  /**
   * Return the gamepad "X" button
   *
   * @return true if the button is pressed
   */
  //  public boolean getGamepadXButton() {
  //    return gameController.getRawButton(GAME_X_BUTTON);
  //  }

  /**
   * Return the gamepad "Y" button
   *
   * @return true if the button is pressed
   */
  //  public boolean getGamepadYButton() {
  //    return gameController.getRawButton(GAME_Y_BUTTON);
  //  }

  /**
   * Return the gamepad "back" button
   *
   * @return true if the button is pressed
   */
  //  public boolean getGamepadBackButton() {
  //    return gameController.getRawButton(GAME_BACK_BUTTON);
  //  }

  /**
   * Return the gamepad "start" button
   *
   * @return true if the button is pressed
   */
  //  public boolean getGamepadStartButton() {
  //    return gameController.getRawButton(GAME_START_BUTTON);
  //  }
}
