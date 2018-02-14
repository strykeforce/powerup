package frc.team2767.control;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.buttons.Trigger;
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

  private final Joystick powerupButtonBoard = new Joystick(0);

  //
  //  Button Board
  //

  private final Button powerupShoulderDownButton =
      new JoystickButton(powerupButtonBoard, POWERUP_SHOULDER_DOWN);
  private final Button powerupShoulderUpButton =
      new JoystickButton(powerupButtonBoard, POWERUP_SHOULDER_UP);
  private final Button powerupLiftDownButton =
      new JoystickButton(powerupButtonBoard, POWERUP_LIFT_DOWN);
  private final Button powerupLiftUpButton =
      new JoystickButton(powerupButtonBoard, POWERUP_LIFT_UP);
  private final Button powerupClimbButton =
      new JoystickButton(powerupButtonBoard, POWERUP_CLIMB_BUTTON);
  private final edu.wpi.first.wpilibj.buttons.Trigger powerupLowScaleButton =
      new edu.wpi.first.wpilibj.buttons.Trigger() {
        @Override
        public boolean get() {
          return getButtonBoardXNeg() < -0.9;
        }
      };
  private final edu.wpi.first.wpilibj.buttons.Trigger powerupScaleHighButton =
      new edu.wpi.first.wpilibj.buttons.Trigger() {
        @Override
        public boolean get() {
          return Math.abs(getButtonBoardYNeg()) > 0.9;
        }
      };
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
  private final edu.wpi.first.wpilibj.buttons.Trigger powerupStowButton =
      new Trigger() {
        @Override
        public boolean get() {
          return getButtonBoardXPos() > 0.9;
        }
      };
  private final Button powerupExchangePosButton =
      new JoystickButton(powerupButtonBoard, POWERUP_EXCHANGE_POS);
  private final Button powerupTransformerButton =
      new JoystickButton(powerupButtonBoard, POWERUP_TRANSFORM);

  @Inject
  public PowerUpControls() {
    powerupScaleHighButton.whenActive(new LiftPosition(17_100));
    powerupScaleMidButton.whenActive(new LiftPosition(13_300));
    powerupLowScaleButton.whenActive(new LiftPosition(9_100));

    powerupStowButton.whenActive(new Stow());

    powerupLiftUpButton.whileActive(new LiftUp());
    powerupLiftDownButton.whileActive(new LiftDown());

    powerupShoulderUpButton.whileActive(new ShoulderUp());
    powerupShoulderDownButton.whileActive(new ShoulderDown());

    powerupExchangePosButton.whenActive(new ShoulderZero());

    powerupPortalIntakeButton.whenActive(new LogCommand("portal intake button"));

    powerupGroundIntakePosButton.whenActive(new ShoulderPosition(-100));

    powerupIntakeInButton.whenActive(new IntakeIn());
    powerupIntakeInButton.whenReleased(new IntakeStop());
    powerupIntakeOutButton.whenActive(new IntakeOut());
    powerupIntakeOutButton.whenReleased(new IntakeStop());

    powerupClimbButton.whenActive(new LogCommand("climb"));

    powerupTransformerButton.whenActive(new LogCommand("transformers!!!!!!"));
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
}
