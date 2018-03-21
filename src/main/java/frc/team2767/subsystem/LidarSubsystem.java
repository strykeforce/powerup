package frc.team2767.subsystem;

import com.ctre.phoenix.CANifier;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.filters.LinearDigitalFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class LidarSubsystem extends Subsystem implements PIDSource {

  private static final Logger logger = LoggerFactory.getLogger(LidarSubsystem.class);
  private static final int NUM_TAPS = 4;
  private final CANifier caNifier;
  private final double dutyCyclePeriod[];
  private final LinearDigitalFilter linearDigitalFilter;

  @Inject
  public LidarSubsystem() {
    caNifier = new CANifier(32);
    dutyCyclePeriod = new double[] {Double.MAX_VALUE, 0};

    linearDigitalFilter = LinearDigitalFilter.movingAverage(this, NUM_TAPS);
  }

  public void end() {
    logger.debug("Ending lidar subsystem");
  }

  @Override
  protected void initDefaultCommand() {}

  public boolean isInRange(double distance) {

    logger.debug("inRange? {}     {} < {}", getDistance() < distance, getDistance(), distance);
    return getDistance() < distance;
  }

  public double getDistance() {
    logger.debug("distance = {}", linearDigitalFilter.pidGet());
    return linearDigitalFilter.pidGet();
  }

  @Override
  public void setPIDSourceType(PIDSourceType pidSource) {}

  @Override
  public PIDSourceType getPIDSourceType() {
    return PIDSourceType.kDisplacement;
  }

  @Override
  public double pidGet() {
    caNifier.getPWMInput(CANifier.PWMChannel.PWMChannel0, dutyCyclePeriod);
    return dutyCyclePeriod[0] / 10.0;
  }

  //
}
