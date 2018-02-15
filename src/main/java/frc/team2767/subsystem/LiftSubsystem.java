package frc.team2767.subsystem;

import static com.ctre.phoenix.motorcontrol.ControlMode.MotionMagic;
import static com.ctre.phoenix.motorcontrol.ControlMode.PercentOutput;

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
import org.strykeforce.thirdcoast.talon.config.StatusFrameRate;
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
  private final double kUpOutput;
  private final double kDownOutput;
  private final double kStopOutput;
  private final int kCloseEnough;
  private final int kJogIncrement;

  private final TalonSRX frontTalon, rearTalon;
  private final Preferences preferences;

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
      StatusFrameRate.GRAPHER.configure(frontTalon);
      StatusFrameRate.GRAPHER.configure(rearTalon);
    }

    Toml toml = settings.getTable(TABLE);
    kUpOutput = toml.getDouble("upOutput");
    kDownOutput = toml.getDouble("downOutput");
    kStopOutput = toml.getDouble("stopOutput");
    kCloseEnough = toml.getLong("closeEnough").intValue();
    kJogIncrement = toml.getLong("jogIncrement").intValue();

    logger.info("upOutput = {}", kUpOutput);
    logger.info("downOutput = {}", kDownOutput);
    logger.info("stopOutput = {}", kStopOutput);
    logger.info("closeEnough = {}", kCloseEnough);
    logger.info("jogIncrement = {}", kJogIncrement);
  }

  public void setPosition(double position) {
    if (position < frontTalon.getSelectedSensorPosition(0)) {
      // going down
      frontTalon.configMotionCruiseVelocity(750, 0);
      frontTalon.configMotionAcceleration(2500, 0);
    } else {
      // going up
      frontTalon.configMotionCruiseVelocity(1500, 0);
      frontTalon.configMotionAcceleration(10000, 0);
    }
    logger.info("setting position = {}", position);
    frontTalon.set(MotionMagic, position);
  }

  public boolean onTarget() {
    return Math.abs(frontTalon.getClosedLoopError(0)) < kCloseEnough;
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

  public void up() {
    int position = frontTalon.getSelectedSensorPosition(0) + kJogIncrement;
    setPosition(position);
  }

  public void down() {
    int position = frontTalon.getSelectedSensorPosition(0) - kJogIncrement;
    if (position < 0) position = 0;
    setPosition(position);
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
