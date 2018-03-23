package frc.team2767.subsystem;

import static com.ctre.phoenix.motorcontrol.ControlMode.PercentOutput;

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
  private static final int RELEASE_ID = 32; // PDP 8
  private static final int TIMEOUT = 10;

  private static final String TABLE = Robot.TABLE + ".INTAKE";
  private static final Logger logger = LoggerFactory.getLogger(IntakeSubsystem.class);
  private final double kLoadOutput;
  private final double kHoldOutput;
  private final double kFastEjectOutput;
  private final double kScaleEjectOutput;
  private final double kSlowEjectOutput;
  private final TalonSRX leftTalon, rightTalon;

  @Inject
  public IntakeSubsystem(Talons talons, Settings settings) {
    leftTalon = talons.getTalon(LEFT_ID);
    rightTalon = talons.getTalon(RIGHT_ID);
    if (rightTalon == null) logger.error("Right Talon missing");
    if (leftTalon == null) logger.error("Left Talon missing");

    Toml toml = settings.getTable(TABLE);
    kLoadOutput = toml.getDouble("loadOutput");
    kHoldOutput = toml.getDouble("holdOutput");
    kFastEjectOutput = toml.getDouble("fastEjectOutput");
    kScaleEjectOutput = toml.getDouble("scaleEjectOutput");
    kSlowEjectOutput = toml.getDouble("slowEjectOutput");

    int zero = toml.getLong("zeroPosition").intValue();
    logger.info("zeroPosition = {}", zero);
    logger.info("loadOutput = {}", kLoadOutput);
    logger.info("holdOutput = {}", kHoldOutput);
    logger.info("fastEjectOutput = {}", kFastEjectOutput);
    logger.info("scaleEjectOutput = {}", kScaleEjectOutput);
    logger.info("slowEjectOutput = {}", kSlowEjectOutput);
  }

  @Override
  public void resetPosition() {}

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
        rightOutput = kHoldOutput;
        logger.debug("running in HOLD at {}", leftOutput);
        break;
      case FAST_EJECT:
        leftOutput = kFastEjectOutput;
        rightOutput = kFastEjectOutput;
        break;
      case SCALE_EJECT:
        leftOutput = kScaleEjectOutput;
        rightOutput = kScaleEjectOutput;
        break;
      case SLOW_EJECT:
        leftOutput = kSlowEjectOutput;
        rightOutput = kSlowEjectOutput;
        break;
    }
    leftTalon.set(PercentOutput, -leftOutput);
    rightTalon.set(PercentOutput, rightOutput);
  }

  public void stop() {
    leftTalon.set(PercentOutput, 0d);
    rightTalon.set(PercentOutput, 0d);
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
