package frc.team2767.subsystem;

import static org.strykeforce.thirdcoast.telemetry.grapher.Measure.*;

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
  private static final int SIDE_LIDAR_CANIFIER_ID = 34;
  private static final double LIDAR_READ_PERIOD = 20.0 / 1000.0;
  private static final int NUM_TAPS = 2;

  private final double kLidarSlope;
  private final double kLidarOffset;
  private final double kLidarSlopeLeft;
  private final double kLidarOffsetLeft;
  private final double kLidarSlopeRight;
  private final double kLidarOffsetRight;

  private final CANifier canifier = new CANifier(CANIFIER_ID);
  private final double lidarPwmData[] = new double[2];
  private final double shoulderPwmData[] = new double[2];
  private final LinearDigitalFilter lidarFilter;

  private boolean lidarEnabled = false;
  private Timer lidarTimer;

  private final CANifier lidarCanifier = new CANifier(SIDE_LIDAR_CANIFIER_ID);
  private final double rightLidarPWMData[] = new double[2];
  private final double leftLidarPWMData[] = new double[2];
  private final LinearDigitalFilter rightLidarFilter;
  private final LinearDigitalFilter leftLidarFilter;

  private Command autonCommand;

  @Inject
  public IntakeSensorsSubsystem(Settings settings) {
    Toml toml = settings.getTable("POWERUP.INTAKE");
    kLidarSlope = toml.getDouble("lidarSlope") / 10.0;
    kLidarOffset = toml.getDouble("lidarOffset");
    kLidarSlopeLeft = toml.getDouble("lidarSlopeLeft") / 10.0;
    kLidarOffsetLeft = toml.getDouble("lidarOffsetLeft");
    kLidarSlopeRight = toml.getDouble("lidarSlopeRight") / 10.0;
    kLidarOffsetRight = toml.getDouble("lidarOffsetRight");

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

    leftLidarFilter =
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
                lidarCanifier.getPWMInput(CANifier.PWMChannel.PWMChannel0, leftLidarPWMData);
                return kLidarSlopeLeft * leftLidarPWMData[0] + kLidarOffsetLeft;
              }
            },
            NUM_TAPS);

    rightLidarFilter =
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
                lidarCanifier.getPWMInput(CANifier.PWMChannel.PWMChannel1, rightLidarPWMData);
                return kLidarSlopeRight * rightLidarPWMData[0] + kLidarOffsetRight;
              }
            },
            NUM_TAPS);

    enableLidar(false);
  }

  @Override
  protected void initDefaultCommand() {}

  @Override
  public void periodic() {
    if (lidarEnabled && lidarTimer.hasPeriodPassed(LIDAR_READ_PERIOD)) {
      lidarFilter.pidGet();
      rightLidarFilter.pidGet();
      leftLidarFilter.pidGet();
    }
  }

  public void enableLidar(boolean enable) {
    lidarEnabled = enable;
    if (enable) {
      logger.info("enabling lidar");
      canifier.setGeneralOutput(GeneralPin.LIMF, true, true);
      canifier.setStatusFramePeriod(CANifierStatusFrame.Status_5_PwmInputs2, 20, 0);
      lidarCanifier.setGeneralOutput(GeneralPin.LIMF, true, true);
      lidarCanifier.setStatusFramePeriod(CANifierStatusFrame.Status_3_PwmInputs0, 20, 0);
      lidarCanifier.setStatusFramePeriod(CANifierStatusFrame.Status_4_PwmInputs1, 20, 0);
      lidarTimer = new Timer();
      lidarTimer.start();
    } else {
      logger.info("disabling lidar");
      canifier.setGeneralOutput(GeneralPin.LIMF, false, true);
      lidarCanifier.setGeneralOutput(GeneralPin.LIMF, false, true);
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

  public double getLeftLidarDistance() {
    return leftLidarFilter.get();
  }

  public double getRightLidarDistance() {
    return rightLidarFilter.get();
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
    return Collections.unmodifiableSet(
        EnumSet.of(PULSE_WIDTH_POSITION, POSITION, QUAD_A_PIN, QUAD_B_PIN));
  }

  @Override
  public DoubleSupplier measurementFor(Measure measure) {
    switch (measure) {
      case PULSE_WIDTH_POSITION:
        return this::getShoulderAbsolutePosition;
      case POSITION:
        return this::getLidarDistance;
      case QUAD_A_PIN:
        return this::getLeftLidarDistance;
      case QUAD_B_PIN:
        return this::getRightLidarDistance;
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
