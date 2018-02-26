package frc.team2767.subsystem;

import static com.ctre.phoenix.motorcontrol.ControlMode.PercentOutput;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.moandjiezana.toml.Toml;
import edu.wpi.first.wpilibj.Servo;
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
public class ClimberSubsystem extends Subsystem implements Graphable {
  private static final int LEFT_ID = 20; // PDP 12
  private static final int RIGHT_ID = 21; // PDP 13
  private static final int SERVO = 0;

  private static final Logger logger = LoggerFactory.getLogger(ClimberSubsystem.class);
  private static final String TABLE = Robot.TABLE + ".CLIMBER";
  private final double kUpFastOutput;
  private final double kUpSlowOutput;
  private final double kStopOutput;
  private final double kUnwindOutput;
  private final double kReleaseOutput;
  private final int kSlowDownPosition;

  private final Servo servo = new Servo(SERVO);
  private final TalonSRX leftTalon, rightTalon;
  private boolean checkPosition;

  @Inject
  public ClimberSubsystem(Talons talons, Settings settings) {
    leftTalon = talons.getTalon(LEFT_ID);
    rightTalon = talons.getTalon(RIGHT_ID);

    if (leftTalon == null || rightTalon == null) logger.error("Talons not present");
    else leftTalon.setSelectedSensorPosition(0, 0, 10);

    servo.set(0d);

    Toml toml = settings.getTable(TABLE);
    kUpFastOutput = toml.getDouble("upFastOutput");
    kUpSlowOutput = toml.getDouble("upSlowOutput");
    kStopOutput = toml.getDouble("stopOutput");
    kUnwindOutput = toml.getDouble("unwindOutput");
    kReleaseOutput = toml.getDouble("releaseOutput");
    kSlowDownPosition = toml.getLong("slowDownPosition").intValue();
    logger.info("upFastOutput = {}", kUpFastOutput);
    logger.info("upSlowOutput = {}", kUpSlowOutput);
    logger.info("stopOutput = {}", kStopOutput);
    logger.info("unwindOutput = {}", kUnwindOutput);
    logger.info("releaseOutput = {}", kReleaseOutput);
    logger.info("slowDownPosition = {}", kSlowDownPosition);
  }

  @Override
  protected void initDefaultCommand() {}

  public void deploy() {
    logger.info("deploying ramp");
    servo.set(0.7);
  }

  public void climb() {
    logger.info("climbing at {}% output", kUpFastOutput * 100);
    leftTalon.set(PercentOutput, kUpFastOutput);
    rightTalon.set(PercentOutput, kUpFastOutput);
    checkPosition = true;
  }

  public boolean isFastClimbFinished() {
    if (!checkPosition) return true;

    int position = leftTalon.getSelectedSensorPosition(0);
    if (position > kSlowDownPosition) {
      logger.info("position = {}, climbing at {}% output", position, kUpSlowOutput * 100);
      leftTalon.set(PercentOutput, kUpSlowOutput);
      rightTalon.set(PercentOutput, kUpSlowOutput);
      checkPosition = false;
    }
    return false;
  }

  public void stop() {
    logger.info("holding at {}% output", kStopOutput * 100);
    leftTalon.set(PercentOutput, kStopOutput);
    rightTalon.set(PercentOutput, kStopOutput);
  }

  public void unwind() {
    logger.info("unwinding at {}% output", kUnwindOutput * 100);
    leftTalon.set(PercentOutput, kUnwindOutput);
    rightTalon.set(PercentOutput, kUnwindOutput);
  }

  public void release() {
    logger.info("releasing at {}% output", kReleaseOutput * 100);
    leftTalon.set(PercentOutput, kReleaseOutput);
    rightTalon.set(PercentOutput, kReleaseOutput);
  }

  @Override
  public void register(TelemetryService telemetryService) {
    if (leftTalon != null)
      telemetryService.register(new TalonItem(leftTalon, "Climber Left (" + LEFT_ID + ")"));
    if (rightTalon != null)
      telemetryService.register(new TalonItem(rightTalon, "Climber Right (" + RIGHT_ID + ")"));
  }
}
