package frc.team2767.subsystem;

import static com.ctre.phoenix.motorcontrol.ControlMode.MotionMagic;
import static com.ctre.phoenix.motorcontrol.ControlMode.PercentOutput;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.moandjiezana.toml.Toml;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.team2767.Robot;
import frc.team2767.Settings;
import frc.team2767.command.shoulder.ShoulderZeroCheck;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.talon.Talons;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;
import org.strykeforce.thirdcoast.telemetry.item.TalonItem;

@Singleton
public class ShoulderSubsystem extends Subsystem implements Graphable, Positionable {
  private static final int ID = 40; // PDP 11

  private static final Logger logger = LoggerFactory.getLogger(ShoulderSubsystem.class);
  private static final String TABLE = Robot.TABLE + ".SHOULDER";
  private static final int TIMEOUT = 10;
  private static final int ABS_TO_REL_RATIO = 5;
  private static final int ENC_EPSILON = 400;
  private static final int STABLE_THRESH = 1;

  private final double kUpOutput;
  private final double kDownOutput;
  private final double kStopOutput;
  private final int kCloseEnough;
  private final int kLimitSwitchZeroPosition;
  private final int kAbsEncoderZeroPosition;

  private final IntakeSensorsSubsystem intakeSensorsSubsystem;
  private final TalonSRX talon;
  private int stableCount;
  private int setpoint;

  @Inject
  public ShoulderSubsystem(
      Talons talons, Settings settings, IntakeSensorsSubsystem intakeSensorsSubsystem) {
    this.intakeSensorsSubsystem = intakeSensorsSubsystem;
    talon = talons.getTalon(ID);
    if (talon == null) {
      logger.error("Talon not present");
    }
    Toml toml = settings.getTable(TABLE);
    kUpOutput = toml.getDouble("upOutput");
    kDownOutput = toml.getDouble("downOutput");
    kStopOutput = toml.getDouble("stopOutput");
    kCloseEnough = toml.getLong("closeEnough").intValue();
    kLimitSwitchZeroPosition = toml.getLong("limitSwitchZeroPosition").intValue();
    kAbsEncoderZeroPosition = toml.getLong("absEncoderZeroPosition").intValue();
    logger.info("closeEnough = {}", kCloseEnough);
    logger.info("limitSwitchZeroPosition = {}", kLimitSwitchZeroPosition);
    logger.info("absEncoderZeroPosition = {}", kAbsEncoderZeroPosition);
    logger.info("upOutput = {}", kUpOutput);
    logger.info("downOutput = {}", kDownOutput);
    logger.info("stopOutput = {}", kStopOutput);
  }

  public void setPosition(int position) {
    setpoint = position;
    logger.debug("positioning to {}", position);
    talon.set(MotionMagic, position);
    stableCount = 0;
  }

  @Override
  public void resetPosition() {
    int position = talon.getSelectedSensorPosition(0);
    logger.info("resetting position = {}", position);
    setPosition(position);
  }

  public boolean onTarget() {
    int error = setpoint - talon.getSelectedSensorPosition(0);
    if (Math.abs(error) > kCloseEnough) stableCount = 0;
    else stableCount++;
    if (stableCount > STABLE_THRESH) {
      logger.debug("stableCount > {}", STABLE_THRESH);
      return true;
    }
    return false;
  }

  public boolean onZero() {
    return talon.getSensorCollection().isFwdLimitSwitchClosed();
  }

  public void positionToZero() {
    logger.info("positioning to zero position");
    talon.set(PercentOutput, kUpOutput);
  }

  public void zeroPositionWithLimitSwitch() {
    logger.info("setting selected sensor to position {}", kLimitSwitchZeroPosition);
    talon.setSelectedSensorPosition(kLimitSwitchZeroPosition, 0, TIMEOUT);
  }

  public void zeroPositionWithEncoderIfNeeded() {
    if (talon.getSelectedSensorVelocity(0) != 0) return;
    int position = talon.getSelectedSensorPosition(0);
    int absolute = intakeSensorsSubsystem.getShoulderAbsolutePosition();
    int error = Math.abs((absolute - kAbsEncoderZeroPosition) * ABS_TO_REL_RATIO - position);
    if (error > ENC_EPSILON) {
      logger.debug("encoder position error = {}, re-zeroing", error);
      zeroPositionWithEncoder();
    }
  }

  public void zeroPositionWithEncoder() {
    if (talon.getSelectedSensorVelocity(0) != 0) return;
    int absolute = intakeSensorsSubsystem.getShoulderAbsolutePosition();
    int positionOffset = absolute - kAbsEncoderZeroPosition;
    talon.setSelectedSensorPosition(ABS_TO_REL_RATIO * positionOffset, 0, 0);
    logger.info("absolute position = {} set position = {}", absolute, positionOffset);
  }

  public void openLoopUp() {
    logger.debug("shoulder up at output {}", kUpOutput);
    talon.set(PercentOutput, kUpOutput);
  }

  public void openLoopDown() {
    logger.debug("shoulder down at output {}", kDownOutput);
    talon.set(PercentOutput, kDownOutput);
  }

  public void stop() {
    logger.debug("shoulder stop at position {}", talon.getSelectedSensorPosition(0));
    talon.set(PercentOutput, kStopOutput);
  }

  @Override
  protected void initDefaultCommand() {
    setDefaultCommand(new ShoulderZeroCheck());
  }

  @Override
  public void register(TelemetryService telemetryService) {
    if (talon != null) telemetryService.register(new TalonItem(talon, "Shoulder (" + ID + ")"));
  }
}
