package frc.team2767.subsystem;

import com.moandjiezana.toml.Toml;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.team2767.Robot;
import frc.team2767.Settings;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ExtenderSubsystem extends Subsystem {
  private static final int RIGHT_ID = 1;
  private static final int LEFT_ID = 2;

  private static final Logger logger = LoggerFactory.getLogger(ExtenderSubsystem.class);
  private static final String TABLE = Robot.TABLE + ".EXTENDER";

  private final double kLeftUpPosition, kLeftDownPosition;
  private final double kRightUpPosition, kRightDownPosition;

  private final Servo rightServo = new Servo(RIGHT_ID);
  private final Servo leftServo = new Servo(LEFT_ID);

  private boolean servoUp = false;

  @Inject
  public ExtenderSubsystem(Settings settings) {
    Toml toml = settings.getTable(TABLE);
    kRightUpPosition = toml.getDouble("rightUpPosition");
    kRightDownPosition = toml.getDouble("rightDownPosition");
    kLeftUpPosition = toml.getDouble("leftUpPosition");
    kLeftDownPosition = toml.getDouble("leftDownPosition");

    logger.info("rightUpPosition = {}", kRightUpPosition);
    logger.info("rightDownPosition = {}", kRightDownPosition);
    logger.info("leftUpPosition = {}", kLeftUpPosition);
    logger.info("leftDownPosition = {}", kLeftDownPosition);
  }

  public void down() {
    logger.debug("right extender {}", kRightDownPosition);
    logger.debug("left extender {}", kLeftDownPosition);
    rightServo.set(kRightDownPosition);
    leftServo.set(kLeftDownPosition);
  }

  public void up() {
    logger.debug("right extender at {}", kRightUpPosition);
    logger.debug("left extender at {}", kLeftUpPosition);
    rightServo.set(kRightUpPosition);
    leftServo.set(kLeftUpPosition);
  }

  public void toggle() {
    if (!servoUp) { // if the servo is below the midpoint of the two positions
      // go up
      logger.debug("right extender at {}", kRightUpPosition);
      logger.debug("left extender at {}", kLeftUpPosition);

      rightServo.set(kRightUpPosition);
      leftServo.set(kLeftUpPosition);
      servoUp = true;
    } else { // if the servo is above the midpoint
      // go down
      logger.debug("going down");
      logger.debug("right extender at {}", kRightDownPosition);
      logger.debug("left extender at {}", kLeftDownPosition);

      rightServo.set(kRightDownPosition);
      leftServo.set(kLeftDownPosition);
      servoUp = false;
    }
  }

  @Override
  protected void initDefaultCommand() {}
}
