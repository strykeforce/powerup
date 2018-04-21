package frc.team2767.motion;

import static edu.wpi.first.wpilibj.DriverStation.Alliance.Red;

import com.google.auto.factory.AutoFactory;
import com.google.auto.factory.Provided;
import com.kauailabs.navx.frc.AHRS;
import com.moandjiezana.toml.Toml;
import com.squareup.moshi.JsonWriter;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Notifier;
import frc.team2767.Settings;
import frc.team2767.command.auton.StartPosition;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Trajectory.Segment;
import jaci.pathfinder.Waypoint;
import java.io.IOException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.DoubleSupplier;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.swerve.SwerveDrive;
import org.strykeforce.thirdcoast.swerve.Wheel;
import org.strykeforce.thirdcoast.telemetry.grapher.Measure;
import org.strykeforce.thirdcoast.telemetry.item.Item;

@AutoFactory
public class PathController implements Runnable, Item {
  private static final Logger logger = LoggerFactory.getLogger(PathController.class);
  private static final int PID = 0;
  private static final double INCHES_PER_METER = 39.3701;
  private static final int NUM_WHEELS = 4;

  private final double kPAzimuth;
  private final double kPDistance;
  private final double kTicksPerMeterRedLeft;
  private final double kTicksPerMeterRedRight;
  private final double kTicksPerMeterBlueLeft;
  private final double kTicksPerMeterBlueRight;

  private final String path;
  private final Trajectory.Config config;
  private final Waypoint[] waypoints;
  private final Trajectory trajectory;
  private final SwerveDrive drive;
  private final Wheel[] wheels;
  private final AHRS gyro;
  private final double kFAccel;
  private Notifier notifier;
  private int[] start = new int[4];
  private int iteration;
  private volatile boolean running;

  private double forward, strafe, azimuth, distance;
  private Segment segment;
  private double ticksPerMeter;
  private double metersPerSecMax;
  private double targetAzimuth;

  /**
   * Runs a PathFinder trajectory.
   *
   * @param path the TOML path description, located in /META-INF/powerup/paths/{path}.toml
   */
  @Inject
  public PathController(String path, @Provided Settings settings, @Provided SwerveDrive drive) {
    this.path = path;
    Toml toml = settings.getPath(path);
    if (toml == null) throw new IllegalArgumentException(path);
    this.drive = drive;
    wheels = drive.getWheels();
    gyro = drive.getGyro();

    config = toml.to(Trajectory.Config.class);

    List<Toml> waypointTomlArray = toml.getTables("waypoints");
    waypoints = new Waypoint[waypointTomlArray.size()];
    for (int i = 0; i < waypoints.length; i++) {
      Toml wp = waypointTomlArray.get(i);
      waypoints[i] =
          new Waypoint(wp.getDouble("x"), wp.getDouble("y"), Math.toRadians(wp.getDouble("angle")));
    }

    long start = System.nanoTime();
    trajectory = Pathfinder.generate(waypoints, config);
    logger.info(
        "CONFIGURE path {}, {} segments in {} ms",
        path,
        trajectory.length(),
        (System.nanoTime() - start) / 1e6);

    toml = settings.getTable("POWERUP.PATH");
    kPAzimuth = toml.getDouble("p_azimuth", 0.0);
    kPDistance = toml.getDouble("p_distance", 0.0);
    kFAccel = toml.getDouble("f_acceleration", 0.0);
    kTicksPerMeterRedLeft = toml.getLong("ticksPerInchRedLeft").doubleValue() * INCHES_PER_METER;
    kTicksPerMeterRedRight = toml.getLong("ticksPerInchRedRight").doubleValue() * INCHES_PER_METER;
    kTicksPerMeterBlueLeft = toml.getLong("ticksPerInchBlueLeft").doubleValue() * INCHES_PER_METER;
    kTicksPerMeterBlueRight =
        toml.getLong("ticksPerInchBlueRight").doubleValue() * INCHES_PER_METER;
    ticksPerMeter = kTicksPerMeterRedLeft;

    logger.info("p_azimuth = {}", kPAzimuth);
    logger.info("p_distance = {}", kPDistance);
    logger.info("f_acceleration = {}", kFAccel);
    logger.info("ticksPerMeterRedLeft = {}", kTicksPerMeterRedLeft);
    logger.info("ticksPerMeterRedRight = {}", kTicksPerMeterRedRight);
    logger.info("ticksPerMeterBlueLeft = {}", kTicksPerMeterBlueLeft);
    logger.info("ticksPerMeterBlueRight = {}", kTicksPerMeterBlueRight);
    logger.info(this.toString());
  }

  public void start(StartPosition startPosition) {
    DriverStation.Alliance alliance = DriverStation.getInstance().getAlliance();
    if (alliance == Red) {
      if (startPosition == StartPosition.LEFT) ticksPerMeter = kTicksPerMeterRedLeft;
      else ticksPerMeter = kTicksPerMeterRedRight;
    } else {
      if (startPosition == StartPosition.LEFT) ticksPerMeter = kTicksPerMeterBlueLeft;
      else ticksPerMeter = kTicksPerMeterBlueRight;
    }
    double ticksPerSecMax = wheels[0].getDriveSetpointMax() * 10.0;
    metersPerSecMax = ticksPerSecMax / ticksPerMeter;
    logger.info(
        "START path {}, target azimuth = {}, alliance = {}, start = {}, tpm = {}",
        path,
        targetAzimuth,
        alliance,
        startPosition,
        ticksPerMeter);

    for (int i = 0; i < NUM_WHEELS; i++) {
      start[i] = wheels[i].getDriveTalon().getSelectedSensorPosition(PID);
    }
    iteration = 1;
    notifier = new Notifier(this);
    notifier.startPeriodic(config.dt);
    running = true;
  }

  public void stop() {
    if (!running) return;
    logger.info("FINISH path {}", path);
    drive.drive(0, 0, 0);
    notifier.stop();
    running = false;
  }

  public boolean isRunning() {
    return running;
  }

  @Override
  public void run() {
    if (iteration == trajectory.length()) {
      stop();
      return;
    }
    segment = trajectory.get(iteration);

    double vel_desired = segment.velocity / metersPerSecMax;
    double vel_setpoint =
        vel_desired + kPDistance * distanceError(segment.position) + kFAccel * segment.acceleration;

    forward = Math.cos(segment.heading) * vel_setpoint;
    strafe = -Math.sin(segment.heading) * vel_setpoint;
    azimuth =
        kPAzimuth * (Math.IEEEremainder(gyro.getAngle(), 360.0) - targetAzimuth); // target = 0 deg

    if (forward > 1d || strafe > 1d) logger.warn("forward = {} strafe = {}", forward, strafe);

    drive.drive(forward, strafe, azimuth);
    iteration++;
  }

  private double distanceError(double position) {
    double desired = ticksPerMeter * position;
    return desired - getDistance();
  }

  public double getDistance() {
    distance = 0;
    for (int i = 0; i < NUM_WHEELS; i++) {
      distance += Math.abs(wheels[i].getDriveTalon().getSelectedSensorPosition(PID) - start[i]);
    }
    distance /= 4;
    return distance;
  }

  public void setTargetAzimuth(double targetAzimuth) {
    this.targetAzimuth = targetAzimuth;
  }

  @Override
  public int deviceId() {
    return 0;
  }

  @Override
  public String type() {
    return "controller";
  }

  @Override
  public String description() {
    return "Path Controller";
  }

  @Override
  public Set<Measure> measures() {
    return Collections.unmodifiableSet(
        EnumSet.of(
            Measure.GYRO_YAW,
            Measure.SWERVE_FORWARD,
            Measure.SWERVE_STRAFE,
            Measure.SWERVE_AZIMUTH,
            Measure.PATH_SEG_X,
            Measure.PATH_SEG_Y,
            Measure.PATH_SEG_POSITION_METERS,
            Measure.PATH_SEG_POSITION_TICKS,
            Measure.PATH_SEG_VELOCITY,
            Measure.PATH_SEG_ACCELERATION,
            Measure.PATH_SEG_JERK,
            Measure.PATH_SEG_HEADING,
            Measure.PATH_DISTANCE,
            Measure.PATH_POSITION_ERROR));
  }

  @Override
  public DoubleSupplier measurementFor(Measure measure) {
    switch (measure) {
      case GYRO_YAW:
        return gyro::getYaw;
      case SWERVE_FORWARD:
        return () -> forward;
      case SWERVE_STRAFE:
        return () -> strafe;
      case SWERVE_AZIMUTH:
        return () -> azimuth;
      case PATH_SEG_X:
        return () -> segment.x;
      case PATH_SEG_Y:
        return () -> segment.y;
      case PATH_SEG_POSITION_METERS:
        return () -> segment.position;
      case PATH_SEG_POSITION_TICKS:
        return () -> kTicksPerMeterRedLeft * segment.position;
      case PATH_SEG_VELOCITY:
        return () -> segment.velocity;
      case PATH_SEG_ACCELERATION:
        return () -> segment.acceleration;
      case PATH_SEG_JERK:
        return () -> segment.jerk;
      case PATH_SEG_HEADING:
        return () -> segment.heading;
      case PATH_DISTANCE:
        return () -> distance;
      case PATH_POSITION_ERROR:
        return () -> kTicksPerMeterRedLeft * segment.position - distance;
      default:
        logger.error("invalid Measure: {}", measure);
    }
    return () -> 0d;
  }

  public double getTicks() {
    int length = trajectory.length();
    Segment segment = trajectory.segments[length - 1];
    logger.debug("distance = {}", segment.position);
    return segment.position * ticksPerMeter;
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
  public String toString() {
    StringBuilder builder = new StringBuilder("[");
    for (Waypoint wp : waypoints) {
      builder
          .append("WayPoint{x=")
          .append(wp.x)
          .append(", y=")
          .append(wp.y)
          .append(", angle=")
          .append(Math.toDegrees(wp.angle))
          .append("}, ");
    }
    builder.delete(builder.length() - 2, builder.length());
    builder.append("]");
    return "PathController{"
        + "config=Trajectory.Config{fit="
        + config.fit
        + ", dt="
        + config.dt
        + ", samples="
        + config.sample_count
        + ", max_velocity="
        + config.max_velocity
        + ", max_acceleration="
        + config.max_acceleration
        + ", max_jerk="
        + config.max_jerk
        + "}, waypoints="
        + builder.toString()
        + '}';
  }
}
