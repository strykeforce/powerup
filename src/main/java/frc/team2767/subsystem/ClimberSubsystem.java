package frc.team2767.subsystem;

import static com.ctre.phoenix.motorcontrol.ControlMode.PercentOutput;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.command.Subsystem;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.talon.Talons;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;
import org.strykeforce.thirdcoast.telemetry.item.TalonItem;

@Singleton
public class ClimberSubsystem extends Subsystem implements Graphable {

  private static final int LEFT_ID = 20; // PDP 12
  private static final int RIGHT_ID = 21; // PDP 13

  private static final double UP_OUTPUT = 1.0;
  private static final double STOP_OUTPUT = 0.1;
  private static final int TIMEOUT = 10;

  private static final Logger logger = LoggerFactory.getLogger(ClimberSubsystem.class);

  private final TalonSRX leftTalon, rightTalon;

  @Inject
  public ClimberSubsystem(Talons talons) {
    this.leftTalon = talons.getTalon(LEFT_ID);
    this.rightTalon = talons.getTalon(RIGHT_ID);
    if (leftTalon == null || rightTalon == null) {
      logger.error("Talons not present");
    }
  }

  @Override
  protected void initDefaultCommand() {}

  public void climb() {
    logger.info("climbing at {}% output", UP_OUTPUT * 100);
    leftTalon.set(PercentOutput, UP_OUTPUT);
    rightTalon.set(PercentOutput, UP_OUTPUT);
  }

  public void hold() {
    logger.info("holding at {}% output", STOP_OUTPUT * 100);
    leftTalon.set(PercentOutput, STOP_OUTPUT);
    rightTalon.set(PercentOutput, STOP_OUTPUT);
  }

  @Override
  public void register(TelemetryService telemetryService) {
    if (leftTalon != null)
      telemetryService.register(new TalonItem(leftTalon, "Climber Left (" + LEFT_ID + ")"));
    if (rightTalon != null)
      telemetryService.register(new TalonItem(rightTalon, "Climber Right (" + RIGHT_ID + ")"));
  }
}
