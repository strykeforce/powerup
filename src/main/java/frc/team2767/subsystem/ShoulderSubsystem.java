package frc.team2767.subsystem;

import static com.ctre.phoenix.motorcontrol.ControlMode.MotionMagic;
import static com.ctre.phoenix.motorcontrol.ControlMode.PercentOutput;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.moandjiezana.toml.Toml;
import edu.wpi.first.wpilibj.Preferences;
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
public class ShoulderSubsystem extends Subsystem implements Graphable, Positionable {
  private static final int ID = 40; // PDP 11

  private static final Logger logger = LoggerFactory.getLogger(ShoulderSubsystem.class);
  private static final String TABLE = Robot.TABLE + ".SHOULDER";
  private static final int TIMEOUT = 10;
  private final Preferences preferences = Preferences.getInstance();

  private final double kUpOutput;
  private final double kDownOutput;
  private final double kStopOutput;
  private final int kCloseEnough;
  private final int kZeroPosition;
  private final int kJogIncrement;

  private final TalonSRX talon;

  @Inject
  public ShoulderSubsystem(Talons talons, Settings settings) {
    talon = talons.getTalon(ID);
    if (talon == null) {
      logger.error("Talon not present");
    }
    Toml toml = settings.getTable(TABLE);
    kUpOutput = toml.getDouble("upOutput");
    kDownOutput = toml.getDouble("downOutput");
    kStopOutput = toml.getDouble("stopOutput");
    kCloseEnough = toml.getLong("closeEnough").intValue();
    kZeroPosition = toml.getLong("zeroPosition").intValue();
    kJogIncrement = toml.getLong("jogIncrement").intValue();
    logger.info("closeEnough = {}", kCloseEnough);
    logger.info("zeroPosition = {}", kZeroPosition);
    logger.info("jogIncrement = {}", kJogIncrement);
  }

  public void setPosition(double position) {
    logger.debug("positioning to {}", position);
    talon.set(MotionMagic, position);
  }

  @Override
  public void resetPosition() {
    int position = talon.getSelectedSensorPosition(0);
    logger.info("resetting position = {}", position);
    setPosition(position);
  }

  public boolean onTarget() {
    return Math.abs(talon.getClosedLoopError(0)) < kCloseEnough;
  }

  public boolean onZero() {
    return talon.getSensorCollection().isFwdLimitSwitchClosed();
  }

  public void positionToZero() {
    logger.info("positioning to zero position");
    talon.set(PercentOutput, kUpOutput);
  }

  public void zeroPosition() {
    logger.info("setting selected sensor to position {}", kZeroPosition);
    talon.setSelectedSensorPosition(kZeroPosition, 0, TIMEOUT);
    down(); // jog down to avoid lift buttresses
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
  protected void initDefaultCommand() {}

  @Override
  public void register(TelemetryService telemetryService) {
    if (talon != null) telemetryService.register(new TalonItem(talon, "Shoulder (" + ID + ")"));
  }
}
