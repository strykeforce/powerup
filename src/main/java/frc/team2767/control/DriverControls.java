package frc.team2767.control;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import frc.team2767.Settings;
import frc.team2767.command.climber.ClimberClimb;
import frc.team2767.command.climber.ClimberDeploy;
import frc.team2767.command.climber.ClimberStop;
import frc.team2767.command.drive.ZeroGyroYawCommand;
import frc.team2767.command.intake.IntakeEject;
import frc.team2767.command.intake.IntakeLoad;
import frc.team2767.command.sequence.DropCube;
import frc.team2767.command.sequence.HoldCube;
import frc.team2767.subsystem.IntakeSubsystem;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;

@SuppressWarnings("unused")
@Singleton
public class DriverControls {

  private static final int USB = 1;

  private final Joystick joystick;

  @Inject
  public DriverControls(Settings settings) {
    if (DriverStation.getInstance().getJoystickName(USB).isEmpty())
      Controls.logger.error("Driver joystick check failed");

    joystick = new Joystick(USB);
    Controls.logger.debug("initializing driver controls with joystick {}", joystick.getName());

    // gyro
    new JoystickButton(joystick, Switch.RESET.index).whenPressed(new ZeroGyroYawCommand());

    // intake
    new JoystickButton(joystick, Shoulder.LEFT_DOWN.index)
        .whenPressed(new IntakeEject(IntakeSubsystem.Mode.FAST_EJECT));
    new JoystickButton(joystick, Shoulder.LEFT_UP.index).whenPressed(new DropCube());

    new JoystickButton(joystick, Shoulder.RIGHT.index)
        .whenPressed(new IntakeLoad(IntakeLoad.Position.GROUND));
    new JoystickButton(joystick, Shoulder.RIGHT.index).whenReleased(new HoldCube());

    // climber
    new JoystickButton(joystick, Climber.DEPLOY.index).whenPressed(new ClimberDeploy());
    new JoystickButton(joystick, Climber.LIFT.index).whenPressed(new ClimberClimb());
    new JoystickButton(joystick, Climber.LIFT.index).whenReleased(new ClimberStop());
  }

  @Nullable
  public SimpleTrigger getAlignWheelsButtons() {
    return joystick != null ? new AlignWheelsTrigger(this) : null;
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

  public enum Climber {
    DEPLOY(17),
    LIFT(16);

    private final int index;

    Climber(int index) {
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
