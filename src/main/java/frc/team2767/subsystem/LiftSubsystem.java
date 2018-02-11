package frc.team2767.subsystem;

import static com.ctre.phoenix.motorcontrol.ControlMode.MotionMagic;
import static com.ctre.phoenix.motorcontrol.ControlMode.PercentOutput;

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
  private final int kAbsoluteTolerance;
  private final TalonSRX frontTalon, rearTalon;
  private final Preferences preferences;
  private int setpoint = 0;
  private int stableCount;

  @Inject
  public LiftSubsystem(Talons talons, Settings settings) {
    this.preferences = Preferences.getInstance();
    //    initializeParameters();

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
    kAbsoluteTolerance = toml.getLong("absoluteTolerance").intValue();

    logger.info("upOutput = {}", kUpOutput);
    logger.info("downOutput = {}", kDownOutput);
    logger.info("stopOutput = {}", kStopOutput);
    logger.info("absoluteTolerance = {}", kAbsoluteTolerance);
  }

  public void setSetpoint(double setpoint) {
    logger.info("setting position = {}", setpoint);
    frontTalon.set(MotionMagic, setpoint);
    this.setpoint = (int) setpoint;
    stableCount = 0;
  }

  public boolean onTarget() {
    if (Math.abs(frontTalon.getSelectedSensorPosition(0) - setpoint) < kAbsoluteTolerance)
      stableCount++;
    else stableCount = 0;
    return stableCount > 2;
  }

  public void zeroPosition() {
    int offset = getAbsolutePosition() - preferences.getInt(ZERO, 0);
    frontTalon.setSelectedSensorPosition(offset, 0, TIMEOUT);
    while (frontTalon.getSelectedSensorPosition(0) > 10) Timer.delay(TIMEOUT / 1000);
    logger.info("zeroing lift, offset = {}", offset);
  }

  public void saveAbsoluteZeroPosition() {
    int pos = getAbsolutePosition();
    preferences.putInt(ZERO, pos);
    logger.info("saved absolute zero = {}", pos);
  }

  public int getAbsolutePosition() {
    if (frontTalon == null) {
      logger.error("front Talon not present, returning 0 for getAzimuthAbsolutePosition()");
      return 0;
    }
    return frontTalon.getSensorCollection().getPulseWidthPosition() & 0xFFF;
  }

  public void up() {
    logger.debug("lift up at output {}", kUpOutput);
    frontTalon.set(PercentOutput, kUpOutput);
  }

  public void down() {
    logger.debug("lift down at output {}", kDownOutput);
    frontTalon.set(PercentOutput, kDownOutput);
  }

  public void stop() {
    logger.info("lift stop at position {}", frontTalon.getSelectedSensorPosition(0));
    frontTalon.set(PercentOutput, kStopOutput);
  }

  public void loadParameters() {
    double p = preferences.getDouble("Lift/0/K_P", 0d);
    double i = preferences.getDouble("Lift/1/K_I", 0d);
    double d = preferences.getDouble("Lift/2/K_D", 0d);
    double f = preferences.getDouble("Lift/3/K_F", 0d);
    int iZone = preferences.getInt("Lift/4/iZone", 0);
    int accel = preferences.getInt("Lift/5/accel", 0);
    int cruise = preferences.getInt("Lift/6/cruise", 0);

    if (frontTalon != null) {
      frontTalon.config_kP(0, p, TIMEOUT);
      frontTalon.config_kI(0, i, TIMEOUT);
      frontTalon.config_kD(0, d, TIMEOUT);
      frontTalon.config_kF(0, f, TIMEOUT);
      frontTalon.config_IntegralZone(0, iZone, TIMEOUT);
      frontTalon.configMotionCruiseVelocity(cruise, TIMEOUT);
      frontTalon.configMotionAcceleration(accel, TIMEOUT);
    }
    logger.info("P = {}", p);
    logger.info("I = {}", i);
    logger.info("D = {}", d);
    logger.info("F = {}", f);
    logger.info("iZone = {}", iZone);
    logger.info("accel = {}", accel);
    logger.info("cruise = {}", cruise);
  }

  public void initializeParameters() {
    preferences.putDouble("Lift/0/K_P", 0d);
    preferences.putDouble("Lift/1/K_I", 0d);
    preferences.putDouble("Lift/2/K_D", 0d);
    preferences.putDouble("Lift/3/K_F", 0d);
    preferences.putInt("Lift/4/iZone", 0);
    preferences.putInt("Lift/5/accel", 0);
    preferences.putInt("Lift/6/cruise", 0);
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

  enum Strategy {
    MOTION,
    PIV
  }
}
