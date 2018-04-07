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
  private static final int LEFT_RELEASE = 0; //PDP 8
  private static final int RIGHT_RELEASE = 1; //PDP 8
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
  private final double kRigthDefault;
  private final double kRightClamp;
  private final double kRightOpen;

  private final TalonSRX leftTalon, rightTalon;
  private final CANifier canifier;

  @Inject
  public IntakeSubsystem(Talons talons, Settings settings, IntakeSensorsSubsystem intakeSensorsSubsystem) {
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
    kRigthDefault = scaleDutyCycle(toml.getDouble("rightServoDefault"));
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
    logger.info("rightServoDefault = {}", kRigthDefault);
    logger.info("rightServoClamp = {}", kRightClamp);
    logger.info("rightServoOpen = {}", kRightOpen);

    canifier.enablePWMOutput(LEFT_RELEASE, true);
    canifier.enablePWMOutput(RIGHT_RELEASE, true);
    canifier.setPWMOutput(LEFT_RELEASE,kLeftDefault);
    canifier.setPWMOutput(RIGHT_RELEASE,kRigthDefault);
  }

  @Override
  public void resetPosition() {}

  public void run(Mode mode) {
    int leftOutput = 0;
    int rightOutput = 0;
    double leftServo = 0;
    double rightServo = 0;
    switch (mode) {
      case LOAD:
        leftOutput = kLoadVelocity;
        rightOutput = kLoadVelocity;
        logger.debug("running in LOAD at {}", leftOutput);
        leftServo = kLeftDefault;
        rightServo = kRigthDefault;
        break;
      case HOLD:
        leftOutput = kHoldVelocity;
        rightOutput = kHoldVelocity;
        logger.debug("running in HOLD at {}", leftOutput);
        leftServo = kLeftClamp;
        rightServo = kRightClamp;
        break;
      case FAST_EJECT:
        leftOutput = kFastEjectVelocity;
        rightOutput = kFastEjectVelocity;
        leftServo = kLeftDefault;
        rightServo = kRigthDefault;
        break;
      case SCALE_EJECT:
        leftOutput = kScaleEjectVelocity;
        rightOutput = kScaleEjectVelocity;
        leftServo = kLeftDefault;
        rightServo = kRigthDefault;
        break;
      case SLOW_EJECT:
        leftOutput = kSlowEjectVelocity;
        rightOutput = kSlowEjectVelocity;
        leftServo = kLeftDefault;
        rightServo = kRigthDefault;
        break;
      case SWITCH_EJECT:
        leftOutput = kSwitchEjectVelocity;
        rightOutput = kSwitchEjectVelocity;
        leftServo = kLeftDefault;
        rightServo = kRigthDefault;
        break;
      case OPEN:
        leftOutput = 0;
        rightOutput = 0;
        leftServo = kLeftOpen;
        rightServo = kRightOpen;
        logger.debug("running in OPEN at {}", leftOutput);
        break;
    }


    int currentLimit = mode == Mode.HOLD ? kHoldCurrentLimit : kNormalCurrentLimit;
    leftTalon.configContinuousCurrentLimit(currentLimit, 0);
    rightTalon.configContinuousCurrentLimit(currentLimit, 0);

    leftTalon.set(Velocity, -leftOutput);
    rightTalon.set(Velocity, rightOutput);
    canifier.setPWMOutput(LEFT_RELEASE,leftServo);
    canifier.setPWMOutput(RIGHT_RELEASE,rightServo);
  }

  public void stop() {
    leftTalon.set(Velocity, 0d);
    rightTalon.set(Velocity, 0d);
    canifier.setPWMOutput(LEFT_RELEASE,kLeftDefault);
    canifier.setPWMOutput(RIGHT_RELEASE,kRigthDefault);
  }

  private double scaleDutyCycle(double Setting){
    return (DUTY_CYCLE_RANGE * Setting + DUTY_CYCLE_MIN)/ DUTY_CYCLE_PERIOD;
  }

  public void servoTest(Mode mode){
    switch(mode){
      case OPEN:
        canifier.setPWMOutput(LEFT_RELEASE,kLeftOpen);
        canifier.setPWMOutput(RIGHT_RELEASE,kRightOpen);
        logger.debug("Opening Intake");
        break;
      case HOLD:
        canifier.setPWMOutput(LEFT_RELEASE,kLeftClamp);
        canifier.setPWMOutput(RIGHT_RELEASE,kRightClamp);
        logger.debug("Clamping Intake");
        break;
      case LOAD:
        canifier.setPWMOutput(LEFT_RELEASE,kLeftDefault);
        canifier.setPWMOutput(RIGHT_RELEASE,kRigthDefault);
        logger.debug("Default Intake");
        break;
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
