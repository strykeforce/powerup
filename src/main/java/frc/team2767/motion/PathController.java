package frc.team2767.motion;

import com.google.auto.factory.AutoFactory;
import com.google.auto.factory.Provided;
import com.kauailabs.navx.frc.AHRS;
import com.moandjiezana.toml.Toml;
import edu.wpi.first.wpilibj.Notifier;
import frc.team2767.Robot;
import frc.team2767.Settings;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Trajectory.Segment;
import jaci.pathfinder.Waypoint;
import java.util.List;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.swerve.SwerveDrive;
import org.strykeforce.thirdcoast.swerve.Wheel;

@AutoFactory
public class PathController implements Runnable {
  private static final Logger logger = LoggerFactory.getLogger(PathController.class);
  private static final String TABLE = "PATHS";
  private static final int PID = 0;
  private static final double INCHES_PER_METER = 39.3701;

  private final double kPAzimuth;
  private final double kPDistance;
  private final double kTicksPerMeter;

  private final Trajectory.Config config;
  private final Waypoint[] waypoints;
  private final Trajectory trajectory;

  private final SwerveDrive drive;
  private final Wheel[] wheels;
  private final AHRS gyro;
  private final Notifier notifier;

  private int[] start = new int[4];
  private int iteration;
  private volatile boolean running;

  /**
   * Runs a PathFinder trajectory.
   *
   * @param path the TOML path description, located in /META-INF/powerup/paths/{path}.toml
   */
  @Inject
  public PathController(String path, @Provided Settings settings, @Provided SwerveDrive drive) {
    settings = Robot.INJECTOR.settings();
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
        (System.nanoTime() - start) / 1_000_000);

    notifier = new Notifier(this);

    toml = settings.getTable("POWERUP.PATH");
    kPAzimuth = toml.getDouble("p_azimuth", 0.0);
    kPDistance = toml.getDouble("p_distance", 0.0);
    kTicksPerMeter = toml.getDouble("ticksPerInch") * INCHES_PER_METER;

    logger.info("p_azimuth = {}", kPAzimuth);
    logger.info("p_distance = {}", kPDistance);
    logger.info("ticksPerMeter = {}", kTicksPerMeter);
    logger.info(this.toString());
  }

  public void start() {
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
    drive.stop();
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
    Segment segment = trajectory.get(iteration);

    double vel_desired = segment.velocity / config.max_velocity;
    double vel_setpoint = vel_desired + kPDistance * distanceError(segment.position);

    double forward = Math.cos(segment.heading) * vel_setpoint;
    double strafe = -Math.sin(segment.heading) * vel_setpoint;
    double azimuth = kPAzimuth * gyro.getYaw(); // target = 0 deg

    if (forward > 1d || strafe > 1d) logger.warn("forward = {} strafe = {}", forward, strafe);

    drive.drive(forward, strafe, azimuth);

    //    logger.debug(
    //        "iteration = {} position = {} velocity = {}, forward = {}, strafe = {} azimuth = {}",
    //        iteration,
    //        segment.position,
    //        segment.velocity,
    //        forward,
    //        strafe,
    //        azimuth);
    iteration++;
  }

  private double distanceError(double position) {
    double desired = kTicksPerMeter * position;
    double distance = 0;

    for (int i = 0; i < 4; i++) {
      distance += Math.abs(wheels[i].getDriveTalon().getSelectedSensorPosition(PID) - start[i]);
    }
    distance /= 4;

    double error = desired - distance;
    logger.debug(
        "distance = {} ticks, position = {} m,  desired = {} ticks, error = {} ticks",
        distance,
        position,
        desired,
        error);
    //    return error;
    return 0; // FIXME: testing only
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
