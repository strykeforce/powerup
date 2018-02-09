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

@Singleton
public class FlipperSubsystem extends Subsystem implements Graphable {
  private static final int ID = 60; // PDP 8

  private static final Logger logger = LoggerFactory.getLogger(FlipperSubsystem.class);
  private static final String TABLE = Robot.TABLE + ".FLIPPER";

  private final double kUpOutput;
  private final double kDownOutput;
  private final int kUpPosition;
  private final int kDownPosition;

  private final TalonSRX talon;

  @Inject
  public FlipperSubsystem(Talons talons, Settings settings) {
    talon = talons.getTalon(ID);
    if (talon != null) talon.setSelectedSensorPosition(0, 0, 10);

    Toml toml = settings.getTable(TABLE);
    kUpOutput = toml.getDouble("upOutput");
    kDownOutput = toml.getDouble("downOutput");
    kUpPosition = toml.getLong("upPosition").intValue();
    kDownPosition = toml.getLong("downPosition").intValue();

    logger.info("upOutput = {}", kUpOutput);
    logger.info("downOutput = {}", kDownOutput);
    logger.info("upPosition = {}", kUpPosition);
    logger.info("downPosition = {}", kDownPosition);
  }

  public void up() {
    logger.debug("flipper up at output {}", kUpOutput);
    talon.set(PercentOutput, kUpOutput);
  }

  public void down() {
    logger.debug("flipper down at output {}", kDownOutput);
    talon.set(PercentOutput, kDownOutput);
  }

  public void stop() {
    logger.debug("flipper stopped");
    talon.set(PercentOutput, 0d);
  }

  public boolean isFinishedUp() {
    return talon.getSelectedSensorPosition(0) > kUpPosition;
  }

  public boolean isFinishedDown() {
    return talon.getSelectedSensorPosition(0) < kDownPosition;
  }

  @Override
  protected void initDefaultCommand() {}

  @Override
  public void register(TelemetryService telemetryService) {
    if (talon != null) telemetryService.register(talon);
  }
}
