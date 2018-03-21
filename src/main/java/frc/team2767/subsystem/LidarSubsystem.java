package frc.team2767.subsystem;

import com.ctre.phoenix.CANifier;
import edu.wpi.first.wpilibj.command.Subsystem;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class LidarSubsystem extends Subsystem {

  private static final Logger logger = LoggerFactory.getLogger(LidarSubsystem.class);
  private final CANifier caNifier;
  private final double dutyCyclePerdiod[];
  //  private final CANifierWrap caNifierWrap;

  @Inject
  public LidarSubsystem() {
    caNifier = new CANifier(32);
    dutyCyclePerdiod = new double[2];
    logger.warn("LIDAR Dist = {}", getDistance());
    logger.warn("{} " + dutyCyclePerdiod[0]);
    //    caNifierWrap = new CANifierWrap();
  }

  public double getDistance() {
    getDutyCycle();
    return dutyCyclePerdiod[0] / 10;
  }

  public void end() {
    logger.debug("Ending lidar subsystem");
  }

  @Override
  protected void initDefaultCommand() {}

  @Override
  public void periodic() {
    //    getDutyCycle();
  }

  private void getDutyCycle() {
    caNifier.getPWMInput(CANifier.PWMChannel.PWMChannel0, dutyCyclePerdiod);
  }
}
