package frc.team2767.subsystem;

import static com.ctre.phoenix.motorcontrol.ControlMode.Velocity;

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

  private static final String TABLE = Robot.TABLE + ".INTAKE";
  private static final Logger logger = LoggerFactory.getLogger(IntakeSubsystem.class);
  private final int kLoadVelocity;
  private final int kHoldVelocity;
  private final int kFastEjectVelocity;
  private final int kScaleEjectVelocity;
  private final int kSlowEjectVelocity;
  private final int kNormalCurrentLimit;
  private final int kHoldCurrentLimit;

  private final TalonSRX leftTalon, rightTalon;

  @Inject
  public IntakeSubsystem(Talons talons, Settings settings) {
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

    kNormalCurrentLimit = toml.getLong("normalCurrentLimit").intValue();
    kHoldCurrentLimit = toml.getLong("holdCurrentLimit").intValue();

    int zero = toml.getLong("zeroPosition").intValue();
    logger.info("zeroPosition = {}", zero);
    logger.info("loadVelocity = {}", kLoadVelocity);
    logger.info("holdVelocity = {}", kHoldVelocity);
    logger.info("fastEjectVelocity = {}", kFastEjectVelocity);
    logger.info("scaleEjectVelocity = {}", kScaleEjectVelocity);
    logger.info("slowEjectVelocity = {}", kSlowEjectVelocity);
  }

  @Override
  public void resetPosition() {}

  public void run(Mode mode) {
    int leftOutput = 0;
    int rightOutput = 0;
    switch (mode) {
      case LOAD:
        leftOutput = kLoadVelocity;
        rightOutput = kLoadVelocity;
        logger.debug("running in LOAD at {}", leftOutput);
        break;
      case HOLD:
        leftOutput = kHoldVelocity;
        rightOutput = kHoldVelocity;
        logger.debug("running in HOLD at {}", leftOutput);
        break;
      case FAST_EJECT:
        leftOutput = kFastEjectVelocity;
        rightOutput = kFastEjectVelocity;
        break;
      case SCALE_EJECT:
        leftOutput = kScaleEjectVelocity;
        rightOutput = kScaleEjectVelocity;
        break;
      case SLOW_EJECT:
        leftOutput = kSlowEjectVelocity;
        rightOutput = kSlowEjectVelocity;
        break;
    }

    int currentLimit = mode == Mode.HOLD ? kHoldCurrentLimit : kNormalCurrentLimit;
    leftTalon.configContinuousCurrentLimit(currentLimit, 0);
    rightTalon.configContinuousCurrentLimit(currentLimit, 0);

    leftTalon.set(Velocity, -leftOutput);
    rightTalon.set(Velocity, rightOutput);
  }

  public void stop() {
    leftTalon.set(Velocity, 0d);
    rightTalon.set(Velocity, 0d);
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
    SLOW_EJECT
  }
}
