package frc.team2767.subsystem;

import com.ctre.phoenix.CANifier;
import com.squareup.moshi.JsonWriter;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.filters.LinearDigitalFilter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;
import org.strykeforce.thirdcoast.telemetry.grapher.Measure;
import org.strykeforce.thirdcoast.telemetry.item.Item;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.DoubleSupplier;

import static org.strykeforce.thirdcoast.telemetry.grapher.Measure.POSITION;
import static org.strykeforce.thirdcoast.telemetry.grapher.Measure.PULSE_WIDTH_POSITION;

@Singleton
public class IntakeSensorsSubsystem extends Subsystem implements Graphable, Item {

  private static final Logger logger = LoggerFactory.getLogger(IntakeSensorsSubsystem.class);

  private static final int CANIFIER_ID = 32;
  private static final double LIDAR_READ_PERIOD = 0.1;
  private static final int NUM_TAPS = 4;

  private final CANifier canifier = new CANifier(CANIFIER_ID);
  private final double lidarPwmData[] = new double[2];
  private final double shoulderPwmData[] = new double[2];
  private final LinearDigitalFilter lidarFilter;

  private boolean lidarEnabled = false;
  private Timer lidarTimer;

  @Inject
  public IntakeSensorsSubsystem() {
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
                canifier.getPWMInput(CANifier.PWMChannel.PWMChannel0, lidarPwmData);
                logger.debug("lidar pulse width = {}", lidarPwmData[0]);
                return lidarPwmData[0] / 10.0;
              }
            },
            NUM_TAPS);
    enableLidar(true);
  }

  @Override
  protected void initDefaultCommand() {}

  @Override
  public void periodic() {
    if (lidarEnabled && lidarTimer.hasPeriodPassed(LIDAR_READ_PERIOD)) lidarFilter.pidGet();
  }

  public void enableLidar(boolean enable) {
    lidarEnabled = enable;
    if (enable) {
      lidarTimer = new Timer();
      lidarTimer.start();
    } else {
      lidarTimer.stop();
      lidarTimer = null;
      lidarFilter.reset();
    }
  }

  public boolean isLidarDisanceWithin(double distance) {
    return lidarFilter.get() < distance;
  }

  public double getLidarDistance() {
    return lidarFilter.get();
  }

  public int getShoulderAbsolutePosition() {
    canifier.getPWMInput(CANifier.PWMChannel.PWMChannel1, shoulderPwmData);
    return (int) ((shoulderPwmData[0] / shoulderPwmData[1]) * 4096d);
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
