package frc.team2767.subsystem;

import static com.ctre.phoenix.motorcontrol.ControlMode.MotionMagic;
import static com.ctre.phoenix.motorcontrol.ControlMode.PercentOutput;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.moandjiezana.toml.Toml;
import edu.wpi.first.wpilibj.Preferences;
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
  private static final int ENC_EPSILON = 350;
  private static final int STABLE_THRESH = 1;
  private final Preferences preferences = Preferences.getInstance();

  private final double kUpOutput;
  private final double kDownOutput;
  private final double kStopOutput;
  private final int kCloseEnough;
  private final int kLimitSwitchZeroPosition;
  private final int kAbsEncoderZeroPosition;
  private final int kJogIncrement;

  private final IntakeSubsystem intakeSubsystem;
  private final TalonSRX talon;
  private int positionOffset;
  private int stableCount;
  private int setpoint;

  @Inject
  public ShoulderSubsystem(Talons talons, Settings settings, IntakeSubsystem intakeSubsystem) {
    this.intakeSubsystem = intakeSubsystem;
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
    kJogIncrement = toml.getLong("jogIncrement").intValue();
    logger.info("closeEnough = {}", kCloseEnough);
    logger.info("limitSwitchZeroPosition = {}", kLimitSwitchZeroPosition);
    logger.info("absEncoderZeroPosition = {}", kAbsEncoderZeroPosition);
    logger.info("jogIncrement = {}", kJogIncrement);
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
    int absolute = intakeSubsystem.getShoulderAbsolutePosition();
    int position = talon.getSelectedSensorPosition(0);
    int error = Math.abs((absolute - kAbsEncoderZeroPosition) * ABS_TO_REL_RATIO - position);
    if (error > ENC_EPSILON) {
      logger.debug("encoder position error = {}, re-zeroing", error);
      zeroPositionWithEncoder();
    }
  }

  public void zeroPositionWithEncoder() {
    if (talon.getSelectedSensorVelocity(0) != 0) return;
    int absolute = intakeSubsystem.getShoulderAbsolutePosition();
    positionOffset = absolute - kAbsEncoderZeroPosition;
    talon.setSelectedSensorPosition(ABS_TO_REL_RATIO * positionOffset, 0, 0);
    logger.info("absolute position = {} set position = {}", absolute, positionOffset);
  }

  public void up() {
    int position = talon.getSelectedSensorPosition(0) + kJogIncrement;
    setPosition(position);
  }

  public void down() {
    int position = talon.getSelectedSensorPosition(0) - kJogIncrement;
    setPosition(position);
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

  public void loadParameters() {
    double p = preferences.getDouble("Shoulder/0/K_P", 0d);
    double i = preferences.getDouble("Shoulder/1/K_I", 0d);
    double d = preferences.getDouble("Shoulder/2/K_D", 0d);
    double f = preferences.getDouble("Shoulder/3/K_F", 0d);
    int iZone = preferences.getInt("Shoulder/4/iZone", 0);
    int accel = preferences.getInt("Shoulder/5/accel", 0);
    int cruise = preferences.getInt("Shoulder/6/cruise", 0);

    if (talon != null) {
      talon.config_kP(0, p, TIMEOUT);
      talon.config_kI(0, i, TIMEOUT);
      talon.config_kD(0, d, TIMEOUT);
      talon.config_kF(0, f, TIMEOUT);
      talon.config_IntegralZone(0, iZone, TIMEOUT);
      talon.configMotionCruiseVelocity(cruise, TIMEOUT);
      talon.configMotionAcceleration(accel, TIMEOUT);
    }
    logger.info("P = {}", p);
    logger.info("I = {}", i);
    logger.info("D = {}", d);
    logger.info("F = {}", f);
    logger.info("iZone = {}", iZone);
    logger.info("accel = {}", accel);
    logger.info("cruise = {}", cruise);
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
