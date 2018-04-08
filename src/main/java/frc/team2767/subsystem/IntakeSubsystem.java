package frc.team2767.subsystem;

import static com.ctre.phoenix.motorcontrol.ControlMode.Velocity;

import com.ctre.phoenix.CANifier;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.moandjiezana.toml.Toml;
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
  private static final int LEFT_RELEASE = 0; // PDP 8
  private static final int RIGHT_RELEASE = 1; // PDP 8
  private static final double DUTY_CYCLE_MIN = 0.5;
  private static final double DUTY_CYCLE_PERIOD = 4.2;
  private static final double DUTY_CYCLE_RANGE = 2.0;

  private static final String TABLE = Robot.TABLE + ".INTAKE";
  private static final Logger logger = LoggerFactory.getLogger(IntakeSubsystem.class);
  private final int kLoadVelocity;
  private final int kHoldVelocity;
  private final int kFastEjectVelocity;
  private final int kScaleEjectVelocity;
  private final int kSlowEjectVelocity;
  private final int kSwitchEjectVelocity;
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

    canifier.enablePWMOutput(LEFT_RELEASE, true);
    canifier.enablePWMOutput(RIGHT_RELEASE, true);
    canifier.setPWMOutput(LEFT_RELEASE, kLeftClamp);
    canifier.setPWMOutput(RIGHT_RELEASE, kRightClamp);
  }

  @Override
  public void resetPosition() {}

  public void run(Mode mode) {
    int output = 0;
    double leftServo = kLeftClamp;
    double rightServo = kRightClamp;
    switch (mode) {
      case LOAD:
        output = kLoadVelocity;
        leftServo = kLeftDefault;
        rightServo = kRightDefault;
        break;
      case HOLD:
        output = kHoldVelocity;
        leftServo = kLeftClamp;
        rightServo = kRightClamp;
        break;
      case FAST_EJECT:
        output = kFastEjectVelocity;
        leftServo = kLeftDefault;
        rightServo = kRightDefault;
        break;
      case SCALE_EJECT:
        output = kScaleEjectVelocity;
        leftServo = kLeftDefault;
        rightServo = kRightDefault;
        break;
      case SLOW_EJECT:
        output = kSlowEjectVelocity;
        leftServo = kLeftDefault;
        rightServo = kRightDefault;
        break;
      case SWITCH_EJECT:
        output = kSwitchEjectVelocity;
        leftServo = kLeftDefault;
        rightServo = kRightDefault;
        break;
      case OPEN:
        leftServo = kLeftOpen;
        rightServo = kRightOpen;
        break;
    }

    logger.info("running in {} at {}", mode, output);

    int currentLimit = mode == Mode.HOLD ? kHoldCurrentLimit : kNormalCurrentLimit;
    leftTalon.configContinuousCurrentLimit(currentLimit, 0);
    rightTalon.configContinuousCurrentLimit(currentLimit, 0);

    leftTalon.set(Velocity, -output);
    rightTalon.set(Velocity, output);
    canifier.setPWMOutput(LEFT_RELEASE, leftServo);
    canifier.setPWMOutput(RIGHT_RELEASE, rightServo);
  }

  public void stop() {
    leftTalon.set(Velocity, 0d);
    rightTalon.set(Velocity, 0d);
    canifier.setPWMOutput(LEFT_RELEASE, kLeftClamp);
    canifier.setPWMOutput(RIGHT_RELEASE, kRightClamp);
  }

  private double scaleDutyCycle(double Setting) {
    return (DUTY_CYCLE_RANGE * Setting + DUTY_CYCLE_MIN) / DUTY_CYCLE_PERIOD;
  }

  public void servoTest(Mode mode) {
    switch (mode) {
      case OPEN:
        canifier.setPWMOutput(LEFT_RELEASE, kLeftOpen);
        canifier.setPWMOutput(RIGHT_RELEASE, kRightOpen);
        logger.debug("Opening Intake");
        break;
      case HOLD:
        canifier.setPWMOutput(LEFT_RELEASE, kLeftClamp);
        canifier.setPWMOutput(RIGHT_RELEASE, kRightClamp);
        logger.debug("Clamping Intake");
        break;
      case LOAD:
        canifier.setPWMOutput(LEFT_RELEASE, kLeftDefault);
        canifier.setPWMOutput(RIGHT_RELEASE, kRightDefault);
        logger.debug("Default Intake");
        break;
      case FAST_EJECT:
      case SCALE_EJECT:
      case SLOW_EJECT:
      case SWITCH_EJECT:
        logger.debug("no test for {}", mode);
    }
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
    SLOW_EJECT,
    SWITCH_EJECT,
    OPEN
  }
}
