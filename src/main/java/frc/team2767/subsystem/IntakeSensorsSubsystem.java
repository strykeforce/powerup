package frc.team2767.subsystem;

import static org.strykeforce.thirdcoast.telemetry.grapher.Measure.POSITION;
import static org.strykeforce.thirdcoast.telemetry.grapher.Measure.PULSE_WIDTH_POSITION;

import com.ctre.phoenix.CANifier;
import com.ctre.phoenix.CANifier.GeneralPin;
import com.ctre.phoenix.CANifierStatusFrame;
import com.moandjiezana.toml.Toml;
import com.squareup.moshi.JsonWriter;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.filters.LinearDigitalFilter;
import frc.team2767.Settings;
import java.io.IOException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.DoubleSupplier;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;
import org.strykeforce.thirdcoast.telemetry.grapher.Measure;
import org.strykeforce.thirdcoast.telemetry.item.Item;

@Singleton
public class IntakeSensorsSubsystem extends Subsystem implements Graphable, Item {

  private static final Logger logger = LoggerFactory.getLogger(IntakeSensorsSubsystem.class);

  private static final int CANIFIER_ID = 32;
  private static final int LIDAR_READ_PERIOD_MS = 10;
  private static final int NUM_TAPS = 2;

  private final double kLidarSlope;
  private final double kLidarOffset;

  private final CANifier canifier = new CANifier(CANIFIER_ID);
  private final double lidarPwmData[] = new double[2];
  private final double shoulderPwmData[] = new double[2];
  private final LinearDigitalFilter lidarFilter;

  private boolean lidarEnabled = false;
  private Timer lidarTimer;

  private Command autonCommand;

  @Inject
  public IntakeSensorsSubsystem(Settings settings) {
    Toml toml = settings.getTable("POWERUP.INTAKE");
    kLidarSlope = toml.getDouble("lidarSlope") / 10.0;
    kLidarOffset = toml.getDouble("lidarOffset");

    lidarFilter =
        LinearDigitalFilter.movingAverage(
            new PIDSource() {
              @Override
              public PIDSourceType getPIDSourceType() {
                return PIDSourceType.kDisplacement;
              }

              @Override
              public void setPIDSourceType(PIDSourceType pidSource) {}

              @Override
              public double pidGet() {
                canifier.getPWMInput(CANifier.PWMChannel.PWMChannel2, lidarPwmData);
                return kLidarSlope * lidarPwmData[0] + kLidarOffset;
              }
            },
            NUM_TAPS);

    enableLidar(false);
  }

  @Override
  protected void initDefaultCommand() {}

  @Override
  public void periodic() {
    if (lidarEnabled && lidarTimer.hasPeriodPassed(LIDAR_READ_PERIOD_MS / 1000.0)) {
      lidarFilter.pidGet();
    }
  }

  public void enableLidar(boolean enable) {
    lidarEnabled = enable;
    if (enable) {
      logger.info("enabling lidar");
      canifier.setGeneralOutput(GeneralPin.LIMF, true, true);
      canifier.setStatusFramePeriod(
          CANifierStatusFrame.Status_5_PwmInputs2, LIDAR_READ_PERIOD_MS, 0);
      lidarTimer = new Timer();
      lidarTimer.start();
    } else {
      logger.info("disabling lidar");
      canifier.setGeneralOutput(GeneralPin.LIMF, false, true);
      if (lidarTimer != null) lidarTimer.stop();
      lidarTimer = null;
      autonCommand = null;
      lidarFilter.reset();
    }
  }

  public boolean isLidarDisanceWithin(int distance) {
    double range = lidarFilter.get();
    if (range == kLidarOffset) autonCommand.cancel();
    return range < distance;
  }

  public double getLidarDistance() {
    return lidarFilter.get();
  }

  public void setAutonCommand(Command autonCommand) {
    this.autonCommand = autonCommand;
  }

  public int getShoulderAbsolutePosition() {
    canifier.getPWMInput(CANifier.PWMChannel.PWMChannel3, shoulderPwmData);
    return (int) ((shoulderPwmData[0] / shoulderPwmData[1]) * 4096d);
  }

  public CANifier getCanifier() {
    return canifier;
  }

  @Override
  public int deviceId() {
    return CANIFIER_ID;
  }

  @Override
  public String type() {
    return "canifier";
  }

  @Override
  public String description() {
    return "Intake Sensors";
  }

  @Override
  public Set<Measure> measures() {
    return Collections.unmodifiableSet(EnumSet.of(PULSE_WIDTH_POSITION, POSITION));
  }

  @Override
  public DoubleSupplier measurementFor(Measure measure) {
    switch (measure) {
      case PULSE_WIDTH_POSITION:
        return this::getShoulderAbsolutePosition;
      case POSITION:
        return this::getLidarDistance;
      default:
        throw new AssertionError(measure);
    }
  }

  @Override
  public void toJson(JsonWriter writer) throws IOException {}

  @Override
  public int compareTo(@NotNull Item other) {
    int result = type().compareTo(other.type());
    if (result != 0) return result;
    return Integer.compare(deviceId(), other.deviceId());
  }

  @Override
  public void register(TelemetryService telemetryService) {
    telemetryService.register(this);
  }
}
