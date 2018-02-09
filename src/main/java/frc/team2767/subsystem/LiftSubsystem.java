package frc.team2767.subsystem;

import static com.ctre.phoenix.motorcontrol.ControlMode.MotionMagic;
import static com.ctre.phoenix.motorcontrol.ControlMode.PercentOutput;
import static com.ctre.phoenix.motorcontrol.ControlMode.Velocity;
import static frc.team2767.subsystem.LiftSubsystem.Strategy.MOTION;
import static frc.team2767.subsystem.LiftSubsystem.Strategy.PIV;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.moandjiezana.toml.Toml;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.PIDSubsystem;
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
public class LiftSubsystem extends PIDSubsystem implements Graphable {
  private static final int FRONT_ID = 50; // PDP 15
  private static final int REAR_ID = 51; // PDP 14

  private static final Strategy strategy = MOTION;

  private static final Logger logger = LoggerFactory.getLogger(LiftSubsystem.class);
  private static final String TABLE = Robot.TABLE + ".LIFT";

  private static final int TIMEOUT = 10;
  private final double kUpOutput;
  private final double kDownOutput;
  private final double kStopOutput;
  private final int kAbsoluteTolerance;
  private final TalonSRX frontTalon, rearTalon;
  private int setpoint = 0;
  private int stableCount;

  @Inject
  public LiftSubsystem(Talons talons, Settings settings) {
    super(0d, 0d, 0d);

    frontTalon = talons.getTalon(FRONT_ID);
    rearTalon = talons.getTalon(REAR_ID);
    if (frontTalon == null || rearTalon == null) {
      logger.error("Talons not present");
    } else {
      rearTalon.follow(frontTalon);
      frontTalon.setSelectedSensorPosition(0, 0, TIMEOUT);
      while (frontTalon.getSelectedSensorPosition(0) > 10) Timer.delay(TIMEOUT / 1000);
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

    switch (strategy) {
      case MOTION:
        logger.info("using MOTION strategy");
        disable();
        getPIDController().free();
        break;
      case PIV:
        logger.info("using PIV strategy");
        setInputRange(0, 1000); // ticks
        setOutputRange(-100d, 100d); // ticks/100ms
        setAbsoluteTolerance(10d); // ticks
        getPIDController().setContinuous(false);
        setSetpoint(0d);
        enable();
        break;
    }
  }

  @Override
  public void setSetpoint(double setpoint) {
    logger.info("setting position = {}", setpoint);
    switch (strategy) {
      case MOTION:
        frontTalon.set(MotionMagic, setpoint);
        this.setpoint = (int) setpoint;
        stableCount = 0;
        break;
      case PIV:
        super.setSetpoint(setpoint);
        break;
    }
  }

  @Override
  public boolean onTarget() {
    switch (strategy) {
      case MOTION:
        if (Math.abs(frontTalon.getSelectedSensorPosition(0) - setpoint) < kAbsoluteTolerance)
          stableCount++;
        else stableCount = 0;
        return stableCount > 2;
      case PIV:
        return super.onTarget();
    }
    return false;
  }

  @Override
  protected double returnPIDInput() {
    return frontTalon.getSelectedSensorPosition(0);
  }

  @Override
  protected void usePIDOutput(double output) {
    if (strategy == PIV) frontTalon.set(Velocity, output);
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

  @Override
  protected void initDefaultCommand() {}

  @Override
  public void register(TelemetryService telemetryService) {
    if (frontTalon != null)
      telemetryService.register(new TalonItem(frontTalon, "Lift Front (" + FRONT_ID + ")"));
    if (rearTalon != null)
      telemetryService.register(new TalonItem(rearTalon, "Lift Rear (" + REAR_ID + ")"));
  }

  enum Strategy {
    MOTION,
    PIV
  }
}
