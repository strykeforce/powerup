package frc.team2767.subsystem;

import static com.ctre.phoenix.motorcontrol.ControlMode.Velocity;

import com.ctre.phoenix.CANifier;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.moandjiezana.toml.Toml;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.team2767.Robot;
import frc.team2767.Settings;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.talon.Talons;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;
import org.strykeforce.thirdcoast.telemetry.item.TalonItem;

@Singleton
public class IntakeSubsystem extends Subsystem implements Graphable, Positionable {
  private static final int LEFT_ID = 30; // PDP 10
  private static final int RIGHT_ID = 31; // PDP 9
  private static final int TIMEOUT = 10;
  private static final int LEFT_RELEASE = 3; // PDP 8
  private static final int RIGHT_RELEASE = 4; // PDP 8
  // private static final double DUTY_CYCLE_MIN = 0.5; //for CANifier
  // private static final double DUTY_CYCLE_PERIOD = 4.2; //for CANifier
  // private static final double DUTY_CYCLE_RANGE = 2.0;  //for CANifier
  private static final double DUTY_CYCLE_MIN = 0.0;
  private static final double DUTY_CYCLE_PERIOD = 1.0;
  private static final double DUTY_CYCLE_RANGE = 1.0;

  private static final String TABLE = Robot.TABLE + ".INTAKE";
  private static final Logger logger = LoggerFactory.getLogger(IntakeSubsystem.class);
  private final int kLoadVelocity;
  private final int kHoldVelocity;
  private final int kFastEjectVelocity;
  private final int kScaleEjectVelocity;
  private final int kSlowEjectVelocity;
  private final int kSwitchEjectVelocity;
  private final int kScaleEjectFastVelocity;
  private final int kNormalCurrentLimit;
  private final int kHoldCurrentLimit;
  private final double kLeftDefault;
  private final double kLeftClamp;
  private final double kLeftOpen;
  private final double kRightDefault;
  private final double kRightClamp;
  private final double kRightOpen;

  private final TalonSRX leftTalon, rightTalon;
  private final CANifier canifier;
  private final Servo leftServo = new Servo(LEFT_RELEASE);
  private final Servo rightServo = new Servo(RIGHT_RELEASE);

  @Inject
  public IntakeSubsystem(
      Talons talons, Settings settings, IntakeSensorsSubsystem intakeSensorsSubsystem) {
    canifier = intakeSensorsSubsystem.getCanifier();
    leftTalon = talons.getTalon(LEFT_ID);
    rightTalon = talons.getTalon(RIGHT_ID);
    if (rightTalon == null) logger.error("Right Talon missing");
    if (leftTalon == null) logger.error("Left Talon missing");

    Toml toml = settings.getTable(TABLE);
    kLoadVelocity = toml.getLong("loadVelocity").intValue();
    kHoldVelocity = toml.getLong("holdVelocity").intValue();
    kFastEjectVelocity = toml.getLong("fastEjectVelocity").intValue();
    kScaleEjectVelocity = toml.getLong("scaleEjectVelocity").intValue();
    kScaleEjectFastVelocity = toml.getLong("scaleEjectFastVelocity").intValue();
    kSlowEjectVelocity = toml.getLong("slowEjectVelocity").intValue();
    kSwitchEjectVelocity = toml.getLong("switchEjectVelocity").intValue();
    kLeftDefault = scaleDutyCycle(toml.getDouble("leftServoDefault"));
    kLeftClamp = scaleDutyCycle(toml.getDouble("leftServoClamp"));
    kLeftOpen = scaleDutyCycle(toml.getDouble("leftServoOpen"));
    kRightDefault = scaleDutyCycle(toml.getDouble("rightServoDefault"));
    kRightClamp = scaleDutyCycle(toml.getDouble("rightServoClamp"));
    kRightOpen = scaleDutyCycle(toml.getDouble("rightServoOpen"));

    kNormalCurrentLimit = toml.getLong("normalCurrentLimit").intValue();
    kHoldCurrentLimit = toml.getLong("holdCurrentLimit").intValue();

    int zero = toml.getLong("zeroPosition").intValue();
    logger.info("zeroPosition = {}", zero);
    logger.info("loadVelocity = {}", kLoadVelocity);
    logger.info("holdVelocity = {}", kHoldVelocity);
    logger.info("fastEjectVelocity = {}", kFastEjectVelocity);
    logger.info("scaleEjectVelocity = {}", kScaleEjectVelocity);
    logger.info("slowEjectVelocity = {}", kSlowEjectVelocity);
    logger.info("switchEjectVelocity = {}", kSwitchEjectVelocity);
    logger.info("leftServoDefault = {}", kLeftDefault);
    logger.info("leftServoClamp = {}", kLeftClamp);
    logger.info("leftServoOpen = {}", kLeftOpen);
    logger.info("rightServoDefault = {}", kRightDefault);
    logger.info("rightServoClamp = {}", kRightClamp);
    logger.info("rightServoOpen = {}", kRightOpen);

    canifier.setPWMOutput(LEFT_RELEASE, kLeftClamp);
    canifier.setPWMOutput(RIGHT_RELEASE, kRightClamp);
  }

  @Override
  public void resetPosition() {}

  public void run(Mode mode) {
    int output = 0;
    double leftServoSetting = kLeftClamp;
    double rightServoSetting = kRightClamp;
    switch (mode) {
      case LOAD:
        output = kLoadVelocity;
        leftServoSetting = kLeftDefault;
        rightServoSetting = kRightDefault;
        break;
      case HOLD:
        output = kHoldVelocity;
        leftServoSetting = kLeftClamp;
        rightServoSetting = kRightClamp;
        break;
      case FAST_EJECT:
        output = kFastEjectVelocity;
        leftServoSetting = kLeftDefault;
        rightServoSetting = kRightDefault;
        break;
      case SCALE_EJECT:
        output = kScaleEjectVelocity;
        leftServoSetting = kLeftDefault;
        rightServoSetting = kRightDefault;
        break;
      case SCALE_EJECT_FAST:
        output = kScaleEjectFastVelocity;
        leftServoSetting = kLeftDefault;
        rightServoSetting = kRightDefault;
        break;
      case SLOW_EJECT:
        output = kSlowEjectVelocity;
        leftServoSetting = kLeftDefault;
        rightServoSetting = kRightDefault;
        break;
      case SWITCH_EJECT:
        output = kSwitchEjectVelocity;
        leftServoSetting = kLeftDefault;
        rightServoSetting = kRightDefault;
        break;
      case OPEN:
        leftServoSetting = kLeftOpen;
        rightServoSetting = kRightOpen;
        break;
    }

    logger.info("running in {} at {}", mode, output);

    int currentLimit = mode == Mode.HOLD ? kHoldCurrentLimit : kNormalCurrentLimit;
    leftTalon.configContinuousCurrentLimit(currentLimit, 0);
    rightTalon.configContinuousCurrentLimit(currentLimit, 0);

    leftTalon.set(Velocity, -output);
    rightTalon.set(Velocity, output);
    canifier.setPWMOutput(LEFT_RELEASE, leftServoSetting);
    canifier.setPWMOutput(RIGHT_RELEASE, rightServoSetting);
    leftServo.set(leftServoSetting);
    rightServo.set(rightServoSetting);
    logger.info("leftServo = {}, rightServo = {}", leftServoSetting, rightServoSetting);
  }

  public void stop() {
    leftTalon.set(Velocity, 0d);
    rightTalon.set(Velocity, 0d);
    canifier.setPWMOutput(LEFT_RELEASE, kLeftClamp);
    canifier.setPWMOutput(RIGHT_RELEASE, kRightClamp);
    leftServo.set(kLeftClamp);
    rightServo.set(kRightClamp);
  }

  public void setEnabled(boolean enabled) {
    canifier.enablePWMOutput(LEFT_RELEASE, enabled);
    canifier.enablePWMOutput(RIGHT_RELEASE, enabled);
  }

  private double scaleDutyCycle(double Setting) {
    return (DUTY_CYCLE_RANGE * Setting + DUTY_CYCLE_MIN) / DUTY_CYCLE_PERIOD;
  }

  @Override
  protected void initDefaultCommand() {}

  @Override
  public void register(TelemetryService telemetryService) {
    if (leftTalon != null)
      telemetryService.register(new TalonItem(leftTalon, "Intake Left (" + LEFT_ID + ")"));
    if (rightTalon != null)
      telemetryService.register(new TalonItem(rightTalon, "Intake Right (" + RIGHT_ID + ")"));
  }

  public enum Mode {
    LOAD,
    HOLD,
    FAST_EJECT,
    SCALE_EJECT,
    SCALE_EJECT_FAST,
    SLOW_EJECT,
    SWITCH_EJECT,
    OPEN
  }
}
