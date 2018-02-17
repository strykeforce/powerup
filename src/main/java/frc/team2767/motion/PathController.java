package frc.team2767.motion;

import com.kauailabs.navx.frc.AHRS;
import com.moandjiezana.toml.Toml;
import edu.wpi.first.wpilibj.Notifier;
import frc.team2767.Robot;
import frc.team2767.subsystem.DriveSubsystem;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Trajectory.Segment;
import jaci.pathfinder.Waypoint;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PathController implements Runnable {

  private static final Logger logger = LoggerFactory.getLogger(PathController.class);
  private static final String TABLE = "PATHS";
  private final Trajectory.Config config;
  private final Waypoint[] waypoints;
  private final Trajectory trajectory;
  private final Notifier notifier;
  private final DriveSubsystem drive;
  private final AHRS gyro;
  private final double kPAzimuth;
  private int iteration;
  private volatile boolean running;

  /**
   * Runs a PathFinder trajectory.
   *
   * @param path the TOML path description, located in /META-INF/powerup/paths/{path}.toml
   */
  public PathController(String path) {
    Toml toml = Robot.INJECTOR.settings().getPath(path);
    if (toml == null) throw new IllegalArgumentException(path);
    drive = Robot.INJECTOR.driveSubsystem();
    gyro = drive.getGyro();
    kPAzimuth = toml.getDouble("p_azimuth", 0.0);
    logger.info("p_azimuth = {}", kPAzimuth);

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

    logger.info(this.toString());
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

  public void start() {
    iteration = 0;
    notifier.startPeriodic(config.dt);
    running = true;
  }

  public boolean isRunning() {
    return running;
  }

  @Override
  public void run() {
    if (iteration == trajectory.length()) {
      running = false;
      notifier.stop();
      logger.debug("notifier is stopped");
      return;
    }
    Segment segment = trajectory.get(iteration);
    double vel_ratio = segment.velocity / config.max_velocity;
    double forward = Math.cos(segment.heading) * vel_ratio;
    double strafe = -Math.sin(segment.heading) * vel_ratio;
    double azimuth = kPAzimuth * gyro.getYaw(); // target = 0 deg
    drive.drive(forward, strafe, azimuth);
    logger.debug(
        "iteration = {} position = {} velocity = {}, forward = {}, strafe = {} azimuth = {}",
        iteration,
        segment.position,
        segment.velocity,
        forward,
        strafe,
        azimuth);
    iteration++;
  }
}
