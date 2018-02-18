package frc.team2767.subsystem;

import static com.ctre.phoenix.motorcontrol.ControlMode.*;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.moandjiezana.toml.Toml;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.team2767.Robot;
import frc.team2767.Settings;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.talon.Errors;
import org.strykeforce.thirdcoast.talon.Talons;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;
import org.strykeforce.thirdcoast.telemetry.item.TalonItem;

@Singleton
public class LiftSubsystem extends Subsystem implements Graphable {
  private static final int MASTER_ID = 50; // PDP 15
  private static final int SLAVE_ID = 51; // PDP 14

  private static final Logger logger = LoggerFactory.getLogger(LiftSubsystem.class);
  private static final String TABLE = Robot.TABLE + ".LIFT";
  private static final String ZERO = "Lift/zero";
  private static final int TIMEOUT = 10;

  private final int kUpAccel;
  private final int kUpVelocity;
  private final int kDownSlowAccel;
  private final int kDownSlowVelocity;
  private final int kDownFastAccel;
  private final int kDownFastVelocity;
  private final int kDownVelocityShiftPos;
  private final double kUpOutput;
  private final double kDownOutput;
  private final double kStopOutput;
  private final int kCloseEnough;
  private final int kJogIncrement;

  private final TalonSRX frontTalon, rearTalon;
  private final Preferences preferences;
  private boolean upward;
  private boolean checkFast;
  private boolean checkSlow;
  private boolean checkEncoder;
  private long positionStartTime;
  private int startPosition;
  private int stableCount;
  private int setpoint;

  @Inject
  public LiftSubsystem(Talons talons, Settings settings) {
    this.preferences = Preferences.getInstance();

    frontTalon = talons.getTalon(MASTER_ID);
    rearTalon = talons.getTalon(SLAVE_ID);

    if (frontTalon == null || rearTalon == null) {
      logger.error("Talons not present");
    } else {
      rearTalon.follow(frontTalon);
      frontTalon.setSelectedSensorPosition(0, 0, TIMEOUT);
      while (frontTalon.getSelectedSensorPosition(0) > 10) Timer.delay(TIMEOUT / 1000);
      logger.info("done setting encoder to zero");
    }

    Toml toml = settings.getTable(TABLE);
    kUpAccel = toml.getLong("upAccel").intValue();
    kUpVelocity = toml.getLong("upVelocity").intValue();
    kDownSlowAccel = toml.getLong("downSlowAccel").intValue();
    kDownSlowVelocity = toml.getLong("downSlowVelocity").intValue();
    kDownFastAccel = toml.getLong("downFastAccel").intValue();
    kDownFastVelocity = toml.getLong("downFastVelocity").intValue();
    kDownVelocityShiftPos = toml.getLong("downVelocityShiftPos").intValue();
    kUpOutput = toml.getDouble("upOutput");
    kDownOutput = toml.getDouble("downOutput");
    kStopOutput = toml.getDouble("stopOutput");
    kCloseEnough = toml.getLong("closeEnough").intValue();
    kJogIncrement = toml.getLong("jogIncrement").intValue();

    logger.info("upAccel = {}", kUpAccel);
    logger.info("upVelocity = {}", kUpVelocity);
    logger.info("downSlowAccel = {}", kDownSlowAccel);
    logger.info("downSlowVelocity = {}", kDownSlowVelocity);
    logger.info("downFastAccel = {}", kDownFastAccel);
    logger.info("downFastVelocity = {}", kDownFastVelocity);
    logger.info("downVelocityShiftPos = {}", kDownVelocityShiftPos);
    logger.info("closeEnough = {}", kCloseEnough);
    logger.info("jogIncrement = {}", kJogIncrement);
    logger.info("upOutput = {}", kUpOutput);
    logger.info("downOutput = {}", kDownOutput);
    logger.info("stopOutput = {}", kStopOutput);
  }

  public void setPosition(int position) {
    setpoint = position;
    startPosition = frontTalon.getSelectedSensorPosition(0);
    logger.info("setting position = {}, starting at {}", position, startPosition);

    upward = setpoint > startPosition;

    if (upward) {
      frontTalon.configMotionCruiseVelocity(kUpVelocity, 0);
      frontTalon.configMotionAcceleration(kUpAccel, 0);
    } else {
      checkFast = checkSlow = true;
      adjustVelocity();
    }

    checkEncoder = true;
    positionStartTime = System.nanoTime();
    stableCount = 0;
    frontTalon.set(MotionMagic, position);
  }

  public void adjustVelocity() {
    int position = frontTalon.getSelectedSensorPosition(0);

    if (checkEncoder) {
      long elapsed = System.nanoTime() - positionStartTime;
      if (elapsed < 200e6) return;

      if (Math.abs(position - startPosition) == 0) {
        frontTalon.set(Disabled, 0);
        setpoint = position;
        logger.error("no encoder movement detected in {} ms", elapsed / 1e6);
        return;
      } else checkEncoder = false;
    }

    if (upward) return;

    if (checkFast && position > kDownVelocityShiftPos) {
      frontTalon.configMotionCruiseVelocity(kDownFastVelocity, 0);
      frontTalon.configMotionAcceleration(kDownFastAccel, 0);
      logger.debug("frontTalon velocity = fast ({}) position = {}", kDownFastVelocity, position);
      checkFast = false;
      return;
    }

    if (checkSlow && position < kDownVelocityShiftPos) {
      frontTalon.configMotionCruiseVelocity(kDownSlowVelocity, 0);
      frontTalon.configMotionAcceleration(kDownSlowAccel, 0);
      logger.debug("frontTalon velocity = slow ({}) position = {}", kDownSlowVelocity, position);
      checkFast = checkSlow = false;
    }
  }

  public boolean onTarget() {
    int error = setpoint - frontTalon.getSelectedSensorPosition(0);
    if (Math.abs(error) > kCloseEnough) stableCount = 0;
    else stableCount++;
    if (stableCount > 3) {
      logger.debug("stableCount > 3");
      return true;
    }
    return false;
  }

  public void up() {
    int position = frontTalon.getSelectedSensorPosition(0) + kJogIncrement;
    setPosition(position);
  }

  public void down() {
    int position = frontTalon.getSelectedSensorPosition(0) - kJogIncrement;
    if (position < 0) position = 0;
    setPosition(position);
  }

  public void zeroPosition() {
    if (frontTalon == null) {
      logger.error("front Talon not present, aborting zeroPosition()");
      return;
    }
    int zero = preferences.getInt(ZERO, 0);
    int setpoint = getAbsolutePosition() - zero;
    ErrorCode e = frontTalon.setSelectedSensorPosition(setpoint, 0, TIMEOUT);
    Errors.check(e, logger);
    frontTalon.set(MotionMagic, setpoint);
    logger.info("zeroed lift position, setpoint = {}", setpoint);
  }

  public void saveAbsoluteZeroPosition() {
    int pos = getAbsolutePosition();
    preferences.putInt(ZERO, pos);
    logger.info("saved absolute zero = {}", pos);
  }

  private int getAbsolutePosition() {
    if (frontTalon == null) {
      logger.error("front Talon not present, returning 0 for getAzimuthAbsolutePosition()");
      return 0;
    }
    return frontTalon.getSensorCollection().getPulseWidthPosition() & 0xFFF;
  }

  public void openLoopUp() {
    logger.debug("lift up at output {}", kUpOutput);
    frontTalon.set(PercentOutput, kUpOutput);
  }

  public void openLoopDown() {
    logger.debug("lift down at output {}", kDownOutput);
    frontTalon.set(PercentOutput, kDownOutput);
  }

  public void stop() {
    logger.info("lift stop at position {}", frontTalon.getSelectedSensorPosition(0));
    frontTalon.set(PercentOutput, kStopOutput);
  }

  @Override
  protected void initDefaultCommand() {}

  @Override
  public void register(TelemetryService telemetryService) {
    if (frontTalon != null)
      telemetryService.register(new TalonItem(frontTalon, "Lift Master (" + MASTER_ID + ")"));
    if (rearTalon != null)
      telemetryService.register(new TalonItem(rearTalon, "Lift Slave (" + SLAVE_ID + ")"));
  }
}
