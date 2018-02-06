package frc.team2767.subsystem;

import static com.ctre.phoenix.motorcontrol.ControlMode.PercentOutput;

import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.moandjiezana.toml.Toml;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.team2767.Settings;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.talon.Talons;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;
import org.strykeforce.thirdcoast.telemetry.item.TalonItem;

@Singleton
public class IntakeSubsystem extends Subsystem implements Graphable {

  private static final int LEFT_ID = 3; // PDP 10
  private static final int RIGHT_ID = 4; // PDP 9
  //  private static final int LEFT_ID = 30; // PDP 10
  //  private static final int RIGHT_ID = 31; // PDP 9

  private static final String TABLE = "POWERUP.INTAKE";
  private static final Logger logger = LoggerFactory.getLogger(IntakeSubsystem.class);
  private final double kLoadPercentOutput;
  private final double kHoldPercentOutput;
  private final TalonSRX leftTalon, rightTalon;
  private final SensorCollection rightSensors;

  @Inject
  public IntakeSubsystem(Talons talons, Settings settings) {
    Toml toml = settings.getTable(TABLE);
    kLoadPercentOutput = toml.getDouble("loadPercentOutput");
    kHoldPercentOutput = toml.getDouble("holdPercentOutput");

    leftTalon = talons.getTalon(LEFT_ID);
    rightTalon = talons.getTalon(RIGHT_ID);
    if (leftTalon == null) {
      logger.error("Left Talon missing");
    }
    if (rightTalon != null) {
      rightSensors = rightTalon.getSensorCollection();
    } else {
      logger.error("Right Talon missing");
      rightSensors = null;
      return;
    }

    logger.info("loadPercentOutput = {}", kLoadPercentOutput);
    logger.info("holdPercentOutput = {}", kHoldPercentOutput);
  }

  public void run(Mode mode) {
    double output = 0d;
    switch (mode) {
      case LOAD:
        output = kLoadPercentOutput;
        break;
      case HOLD:
        output = kHoldPercentOutput;
        break;
    }
    leftTalon.set(PercentOutput, output);
    rightTalon.set(PercentOutput, output);
  }

  public void stop() {
    leftTalon.set(PercentOutput, 0d);
    rightTalon.set(PercentOutput, 0d);
  }

  public boolean isLoaded() {
    return rightSensors.isFwdLimitSwitchClosed();
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
    HOLD;
  }
}
