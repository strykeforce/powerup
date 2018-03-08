package frc.team2767.subsystem;

import static com.ctre.phoenix.motorcontrol.ControlMode.PercentOutput;

import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.moandjiezana.toml.Toml;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
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
  private static final int RELEASE_ID = 32; // PDP 8
  private static final int TIMEOUT = 10;

  private static final String TABLE = Robot.TABLE + ".INTAKE";
  private static final Logger logger = LoggerFactory.getLogger(IntakeSubsystem.class);
  private final double kLoadOutput;
  private final double kHoldOutput;
  private final double kFastEjectOutput;
  private final double kSlowEjectOutput;
  private final int kOpenPosition;
  private final TalonSRX leftTalon, rightTalon;
  //  private final TalonSRX releaseTalon;
  private final SensorCollection cubeSensors;
  private final SensorCollection shoulderAbsoluteSensor;

  @Inject
  public IntakeSubsystem(Talons talons, Settings settings) {
    leftTalon = talons.getTalon(LEFT_ID);
    rightTalon = talons.getTalon(RIGHT_ID);
    //    releaseTalon = talons.getTalon(RELEASE_ID);
    if (rightTalon == null) logger.error("Right Talon missing");
    //    if (releaseTalon == null) logger.error("Release Talon missing");
    if (rightTalon != null) {
      shoulderAbsoluteSensor = rightTalon.getSensorCollection();
    } else {
      logger.error("Right Talon missing");
      shoulderAbsoluteSensor = null;
    }
    if (leftTalon != null) {
      cubeSensors = leftTalon.getSensorCollection();
    } else {
      logger.error("Right Talon missing");
      cubeSensors = null;
    }

    Toml toml = settings.getTable(TABLE);
    kLoadOutput = toml.getDouble("loadOutput");
    kHoldOutput = toml.getDouble("holdOutput");
    kFastEjectOutput = toml.getDouble("fastEjectOutput");
    kSlowEjectOutput = toml.getDouble("slowEjectOutput");
    kOpenPosition = toml.getLong("openPosition").intValue();

    int zero = toml.getLong("zeroPosition").intValue();
    //    int absolute = releaseTalon.getSensorCollection().getPulseWidthPosition() & 0xFFF;
    //    releaseTalon.setSelectedSensorPosition(absolute - zero, 0, TIMEOUT);
    logger.info("zeroPosition = {}", zero);
    //    logger.info("set RELEASE zero position, current position = {}", absolute - zero);
    logger.info("loadOutput = {}", kLoadOutput);
    logger.info("holdOutput = {}", kHoldOutput);
    logger.info("fastEjectOutput = {}", kFastEjectOutput);
    logger.info("slowEjectOutput = {}", kSlowEjectOutput);
    logger.info("openPosition = {}", kOpenPosition);
  }
  //
  //  public void open() {
  //    releaseTalon.set(MotionMagic, kOpenPosition);
  //    logger.debug("set release to open position");
  //  }
  //
  //  public void close() {
  //    releaseTalon.set(MotionMagic, 0);
  //    logger.debug("set release to closed position");
  //  }

  @Override
  public void resetPosition() {
    //    releaseTalon.set(MotionMagic, releaseTalon.getSelectedSensorPosition(0));
  }

  public void run(Mode mode) {
    double leftOutput = 0d;
    double rightOutput = 0d;
    switch (mode) {
      case LOAD:
        leftOutput = kLoadOutput;
        rightOutput = kLoadOutput;
        logger.debug("running in LOAD at {}", leftOutput);
        break;
      case HOLD:
        leftOutput = kHoldOutput;
        rightOutput = 0.25 * kHoldOutput;
        logger.debug("running in HOLD at {}", leftOutput);
        break;
      case FAST_EJECT:
        leftOutput = kFastEjectOutput;
        rightOutput = kFastEjectOutput;
        break;
      case SLOW_EJECT:
        leftOutput = kSlowEjectOutput;
        rightOutput = kSlowEjectOutput;
        break;
    }
    leftTalon.set(PercentOutput, leftOutput);
    rightTalon.set(PercentOutput, rightOutput);
  }

  public void stop() {
    leftTalon.set(PercentOutput, 0d);
    rightTalon.set(PercentOutput, 0d);
  }

  public int getShoulderAbsolutePosition() {
    return shoulderAbsoluteSensor.getPulseWidthPosition() & 0xFFF;
  }

  public boolean isLoaded() {
    return cubeSensors.isFwdLimitSwitchClosed() && cubeSensors.isRevLimitSwitchClosed();
  }

  @Override
  protected void initDefaultCommand() {}

  @Override
  public void periodic() {
    // every time the scheduler is run, display the limit switch value.
    SmartDashboard.putBoolean(
        "Block obtained?", rightTalon.getSensorCollection().isRevLimitSwitchClosed());
  }

  @Override
  public void register(TelemetryService telemetryService) {
    if (leftTalon != null)
      telemetryService.register(new TalonItem(leftTalon, "Intake Left (" + LEFT_ID + ")"));
    if (rightTalon != null)
      telemetryService.register(new TalonItem(rightTalon, "Intake Right (" + RIGHT_ID + ")"));
    //    if (releaseTalon != null)
    //      telemetryService.register(new TalonItem(releaseTalon, "Intake Release (" + RELEASE_ID +
    // ")"));
  }

  public enum Mode {
    LOAD,
    HOLD,
    FAST_EJECT,
    SLOW_EJECT
  }
}
