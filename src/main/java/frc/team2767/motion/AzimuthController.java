package frc.team2767.motion;

import com.google.auto.factory.AutoFactory;
import com.google.auto.factory.Provided;
import com.kauailabs.navx.frc.AHRS;
import com.moandjiezana.toml.Toml;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import frc.team2767.Settings;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.DoubleSupplier;
import javax.inject.Inject;
import org.strykeforce.thirdcoast.swerve.SwerveDrive;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;
import org.strykeforce.thirdcoast.telemetry.grapher.Measure;
import org.strykeforce.thirdcoast.telemetry.item.AbstractItem;

@AutoFactory
public class AzimuthController extends AbstractItem {

  private static final Set<Measure> MEASURES =
      Collections.unmodifiableSet(
          EnumSet.of(Measure.GYRO_YAW, Measure.CLOSED_LOOP_ERROR, Measure.CLOSED_LOOP_TARGET));

  private final PIDController pid;
  private final AHRS gyro;

  private double setpoint;

  @Inject
  public AzimuthController(
      @Provided Settings settings,
      @Provided SwerveDrive swerveDrive,
      @Provided TelemetryService telemetryService,
      PIDOutput output) {
    super("controller", "Azimuth Controller", MEASURES);
    gyro = swerveDrive.getGyro();
    Toml toml = settings.getTable("POWERUP.AZIMUTH");
    double p = toml.getDouble("p");
    double i = toml.getDouble("i");
    double d = toml.getDouble("d");
    double tol = toml.getDouble("tolerance");
    double outputMax = toml.getDouble("outputMax");
    pid = new PIDController(p, i, d, gyro, output, 0.01);
    pid.setInputRange(-180d, 180d);
    pid.setOutputRange(-outputMax, outputMax);
    pid.setContinuous(true);
    pid.setAbsoluteTolerance(tol);
  }

  public boolean onTarget() {
    return pid.onTarget();
  }

  public void enable() {
    pid.enable();
  }

  public void disable() {
    pid.disable();
  }

  public double getSetpoint() {
    return setpoint;
  }

  public void setSetpoint(double setpoint) {
    this.setpoint = setpoint;
    pid.setSetpoint(setpoint);
  }

  public double getYaw() {
    return gyro.getYaw();
  }

  @Override
  public int deviceId() {
    return 1;
  }

  @Override
  public DoubleSupplier measurementFor(Measure measure) {
    switch (measure) {
      case GYRO_YAW:
        return gyro::getYaw;
      case CLOSED_LOOP_TARGET:
        return this::getSetpoint;
      case CLOSED_LOOP_ERROR:
        return pid::getError;
      default:
        return () -> 0;
    }
  }
}
