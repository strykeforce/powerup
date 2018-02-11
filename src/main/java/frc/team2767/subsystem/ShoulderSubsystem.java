package frc.team2767.subsystem;

import static com.ctre.phoenix.motorcontrol.ControlMode.PercentOutput;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.moandjiezana.toml.Toml;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.team2767.Robot;
import frc.team2767.Settings;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.talon.Talons;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;
import org.strykeforce.thirdcoast.telemetry.item.TalonItem;

public class ShoulderSubsystem extends Subsystem implements Graphable {
  private static final int ID = 40; // PDP 11

  private static final Logger logger = LoggerFactory.getLogger(ShoulderSubsystem.class);
  private static final String TABLE = Robot.TABLE + ".SHOULDER";

  private final double kUpOutput;
  private final double kDownOutput;
  private final double kStopOutput;

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
  }

  public void up() {
    logger.debug("shoulder up at output {}", kUpOutput);
    talon.set(PercentOutput, kUpOutput);
  }

  public void down() {
    logger.debug("shoulder down at output {}", kDownOutput);
    talon.set(PercentOutput, kDownOutput);
  }

  public void stop() {
    logger.info("shoulder stop at position {}", talon.getSelectedSensorPosition(0));
    talon.set(PercentOutput, kStopOutput);
  }

  @Override
  protected void initDefaultCommand() {}

  @Override
  public void register(TelemetryService telemetryService) {
    if (talon != null) telemetryService.register(new TalonItem(talon, "Shoulder (" + ID + ")"));
  }
}
