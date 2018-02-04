package frc.team2767.subsystem;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.command.Subsystem;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.talon.Talons;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;

@Singleton
public class IntakeSubsystem extends Subsystem implements Graphable {

  private static final int LEFT_ID = 30; // PDP 10
  private static final int RIGHT_ID = 31; // PDP 9

  private static final Logger logger = LoggerFactory.getLogger(IntakeSubsystem.class);

  private final TalonSRX leftTalon, rightTalon;

  @Inject
  public IntakeSubsystem(Talons talons) {
    leftTalon = talons.getTalon(LEFT_ID);
    rightTalon = talons.getTalon(RIGHT_ID);
    if (leftTalon == null || rightTalon == null) {
      logger.error("Talons not present");
    }
  }

  @Override
  protected void initDefaultCommand() {}

  @Override
  public void register(TelemetryService telemetryService) {
    telemetryService.register(leftTalon);
    telemetryService.register(rightTalon);
  }
}
