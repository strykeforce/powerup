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

  private final double kPAzimuth;
  private final double kPDistance;
  private final double kTicksPerMeterRed;
  private final double kTicksPerMeterBlue;
  private final Trajectory.Config config;
  private final Waypoint[] waypoints;
  private final Trajectory trajectory;
  private final SwerveDrive drive;
  private final Wheel[] wheels;
  private final AHRS gyro;
  private final Notifier notifier;
  private final double kPAccel;
  private int[] start = new int[4];
  private int iteration;
  private volatile boolean running;

  private double forward, strafe, azimuth, distance;
  private Segment segment;
  private double ticksPerMeter;
  private double metersPerSecMax;

  /**
   * Runs a PathFinder trajectory.
   *
   * @param path the TOML path description, located in /META-INF/powerup/paths/{path}.toml
   */
  @Inject
  public PathController(String path, @Provided Settings settings, @Provided SwerveDrive drive) {
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
        "{} generated {} segments in {} ms",
        path,
        trajectory.length(),
        (System.nanoTime() - start) / 1e6);

    notifier = new Notifier(this);

    toml = settings.getTable("POWERUP.PATH");
    kPAzimuth = toml.getDouble("p_azimuth", 0.0);
    kPDistance = toml.getDouble("p_distance", 0.0);
    kPAccel = toml.getDouble("p_acceleration", 0.0);
    kTicksPerMeterRed = toml.getLong("ticksPerInchRed").doubleValue() * INCHES_PER_METER;
    kTicksPerMeterBlue = toml.getLong("ticksPerInchBlue").doubleValue() * INCHES_PER_METER;
    ticksPerMeter = kTicksPerMeterRed;

    logger.info("p_azimuth = {}", kPAzimuth);
    logger.info("p_distance = {}", kPDistance);
    logger.info("p_acceleration = {}", kPAccel);
    logger.info("ticksPerMeterRed = {}", kTicksPerMeterRed);
    logger.info("ticksPerMeterBlue = {}", kTicksPerMeterBlue);
    logger.info(this.toString());
  }

  public void start() {
    logger.info("P_Accel = {}", kPAccel);
    DriverStation.Alliance alliance = DriverStation.getInstance().getAlliance();
    ticksPerMeter = alliance == Red ? kTicksPerMeterRed : kTicksPerMeterBlue;
    double ticksPerSecMax = wheels[0].getDriveSetpointMax() * 10.0;
    metersPerSecMax = ticksPerSecMax / ticksPerMeter;
    logger.info(
        "{} alliance, ticks per meter = {}, max vel = {} m/s",
        alliance,
        ticksPerMeter,
        metersPerSecMax);

    for (int i = 0; i < 4; i++) {
      start[i] = wheels[i].getDriveTalon().getSelectedSensorPosition(PID);
    }
    iteration = 0;
    notifier.startPeriodic(config.dt);
    running = true;
  }

  public void stop() {
    logger.debug("stopping path controller and swerve drive");
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
        vel_desired + kPDistance * distanceError(segment.position) + kPAccel * segment.acceleration;

    forward = Math.cos(segment.heading) * vel_setpoint;
    strafe = -Math.sin(segment.heading) * vel_setpoint;
    azimuth = kPAzimuth * gyro.getYaw(); // target = 0 deg

    if (forward > 1d || strafe > 1d) logger.warn("forward = {} strafe = {}", forward, strafe);

    drive.drive(forward, strafe, azimuth);
    iteration++;
  }

  private double distanceError(double position) {
    double desired = ticksPerMeter * position;
    distance = 0;

    for (int i = 0; i < 4; i++) {
      distance += Math.abs(wheels[i].getDriveTalon().getSelectedSensorPosition(PID) - start[i]);
    }
    distance /= 4;

    double error = desired - distance;
    //    logger.debug(
    //        "distance = {} ticks, position = {} m,  desired = {} ticks, error = {} ticks",
    //        distance,
    //        position,
    //        desired,
    //        error);
    return error;
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
        return () -> kTicksPerMeterRed * segment.position;
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
        return () -> kTicksPerMeterRed * segment.position - distance;
      default:
        logger.error("invalid Measure: {}", measure);
    }
    return () -> 0d;
  }

  @Override
  public void toJson(JsonWriter writer) throws IOException {}

  @Override
  public int compareTo(@NotNull Item o) {
    return 0;
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
