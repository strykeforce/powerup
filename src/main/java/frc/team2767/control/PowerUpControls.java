package frc.team2767.control;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.buttons.Trigger;
import frc.team2767.Settings;
import frc.team2767.command.climber.ClimberClimb;
import frc.team2767.command.climber.ClimberDeploy;
import frc.team2767.command.climber.ClimberStop;
import frc.team2767.command.extender.ExtenderToggle;
import frc.team2767.command.intake.IntakeIn;
import frc.team2767.command.intake.IntakeOut;
import frc.team2767.command.intake.IntakeStop;
import frc.team2767.command.lift.LiftDown;
import frc.team2767.command.lift.LiftPosition;
import frc.team2767.command.lift.LiftStop;
import frc.team2767.command.lift.LiftUp;
import frc.team2767.command.sequence.Stow;
import frc.team2767.command.shoulder.*;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PowerUpControls {

  private static final int USB = 0;

  private static final double AXIS_THRESH = 0.9;
  private final Joystick board;

  @Inject
  PowerUpControls(Settings settings) {
    if (DriverStation.getInstance().getJoystickName(USB).isEmpty())
      Controls.logger.error("POWER UP joystick check failed");

    board = new Joystick(USB);
    Controls.logger.debug("initializing POWER UP button board with joystick {}", board.getName());

    // intake
    Button intakeIn = new JoystickButton(board, Switch.INTAKE_IN.index);
    intakeIn.whenActive(new IntakeIn());
    intakeIn.whenReleased(new IntakeStop());

    Button intakeOut = new JoystickButton(board, Switch.INTAKE_OUT.index);
    intakeOut.whenActive(new IntakeOut());
    intakeOut.whenReleased(new IntakeStop());

    // climber
    new JoystickButton(board, Switch.CLIMBER_DEPLOY.index).whenPressed(new ClimberDeploy());
    new JoystickButton(board, Switch.CLIMB.index).whenPressed(new ClimberClimb());
    new JoystickButton(board, Switch.CLIMB.index).whenReleased(new ClimberStop());

    // lift
    new JoystickButton(board, Switch.LIFT_UP.index).whenPressed(new LiftUp());
    new JoystickButton(board, Switch.LIFT_UP.index).whenReleased(new LiftStop());
    new JoystickButton(board, Switch.LIFT_DOWN.index).whenPressed(new LiftDown());
    new JoystickButton(board, Switch.LIFT_DOWN.index).whenReleased(new LiftStop());

    new Trigger() {
      @Override
      public boolean get() {
        return board.getRawAxis(Axis.STOW.index) > AXIS_THRESH;
      }
    }.whenActive(new Stow());

    new Trigger() {
      @Override
      public boolean get() {
        return board.getRawAxis(Axis.LIFT_LOW_SCALE.index) < -AXIS_THRESH;
      }
    }.whenActive(new LiftPosition(LiftPosition.Position.SCALE_LOW));

    new Trigger() { // TODO: check sign of axis
      @Override
      public boolean get() {
        return Math.abs(board.getRawAxis(Axis.LIFT_HIGH_SCALE.index)) > AXIS_THRESH;
      }
    }.whenActive(new LiftPosition(LiftPosition.Position.SCALE_HIGH));

    new JoystickButton(board, Switch.LIFT_MID_SCALE.index)
        .whenActive(new LiftPosition(LiftPosition.Position.SCALE_MID));
    new JoystickButton(board, Switch.SHOULDER_ZERO.index).whenActive(new ShoulderZeroWithEncoder());
    new JoystickButton(board, Switch.GROUND_INTAKE_POS.index)
        .whenActive(new ShoulderPosition(ShoulderPosition.Position.INTAKE));
    new JoystickButton(board, Switch.EXTENDER.index).whenActive(new ExtenderToggle());

    // shoulder
    new JoystickButton(board, Switch.SHOULDER_UP.index).whenPressed(new ShoulderUp());
    new JoystickButton(board, Switch.SHOULDER_UP.index).whenReleased(new ShoulderStop());
    new JoystickButton(board, Switch.SHOULDER_DOWN.index).whenPressed(new ShoulderDown());
    new JoystickButton(board, Switch.SHOULDER_DOWN.index).whenReleased(new ShoulderStop());
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
    CLIMBER_DEPLOY(6),
    LIFT_MID_SCALE(7),
    EXTENDER(8),
    INTAKE_IN(11),
    INTAKE_OUT(9),
    GROUND_INTAKE_POS(10),
    SHOULDER_ZERO(12);
    private final int index;

    Switch(int index) {
      this.index = index;
    }
  }
}
