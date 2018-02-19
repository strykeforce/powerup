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
  private final double kUpOutput;
  private final double kStopOutput;
  private final double kUnwindOutput;
  private final Servo servo = new Servo(SERVO);
  private final TalonSRX leftTalon, rightTalon;

  @Inject
  public ClimberSubsystem(Talons talons, Settings settings) {
    servo.set(0d);
    leftTalon = talons.getTalon(LEFT_ID);
    rightTalon = talons.getTalon(RIGHT_ID);
    if (leftTalon == null || rightTalon == null) {
      logger.error("Talons not present");
    }
    Toml toml = settings.getTable(TABLE);
    kUpOutput = toml.getDouble("upOutput");
    kStopOutput = toml.getDouble("stopOutput");
    kUnwindOutput = -0.15;
    logger.info("upOutput = {}", kUpOutput);
    logger.info("stopOutput = {}", kStopOutput);
  }

  @Override
  protected void initDefaultCommand() {}

  public void deploy() {
    servo.set(1d);
  }

  public void climb() {
    logger.info("climbing at {}% output", kUpOutput * 100);
    leftTalon.set(PercentOutput, kUpOutput);
    rightTalon.set(PercentOutput, kUpOutput);
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

  @Override
  public void register(TelemetryService telemetryService) {
    if (leftTalon != null)
      telemetryService.register(new TalonItem(leftTalon, "Climber Left (" + LEFT_ID + ")"));
    if (rightTalon != null)
      telemetryService.register(new TalonItem(rightTalon, "Climber Right (" + RIGHT_ID + ")"));
  }
}
