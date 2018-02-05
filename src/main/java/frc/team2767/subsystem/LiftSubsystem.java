package frc.team2767.subsystem;

import static com.ctre.phoenix.motorcontrol.ControlMode.PercentOutput;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.team2767.Robot;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.talon.Talons;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;
import org.strykeforce.thirdcoast.telemetry.item.TalonItem;

@Singleton
public class LiftSubsystem extends Subsystem implements Graphable {

  private static final Logger logger = LoggerFactory.getLogger(LiftSubsystem.class);

  private static final int FRONT_ID = 50; // PDP 15
  private static final int REAR_ID = 51; // PDP 14
  private static final double UP_OUTPUT = 0.2;
  private static final double DOWN_OUTPUT = -0.2;
  private static final double STOP_OUTPUT = 0.0;
  private static final int TIMEOUT = 10;

  private final TalonSRX frontTalon, rearTalon;

  @Inject
  public LiftSubsystem() {
    Talons talons = Robot.INJECTOR.talons();
    frontTalon = talons.getTalon(FRONT_ID);
    rearTalon = talons.getTalon(REAR_ID);
    if (frontTalon == null || rearTalon == null) {
      logger.error("Talons not present");
      return;
    }
    frontTalon.setSelectedSensorPosition(0, 0, TIMEOUT);
  }

  @Override
  protected void initDefaultCommand() {}

  public void up() {
    logger.info("sending UP command");
    frontTalon.set(PercentOutput, UP_OUTPUT);
    rearTalon.set(PercentOutput, UP_OUTPUT);
  }

  public void down() {
    logger.info("sending DOWN command");
    frontTalon.set(PercentOutput, DOWN_OUTPUT);
    rearTalon.set(PercentOutput, DOWN_OUTPUT);
  }

  public void stop() {
    logger.info("sending STAHP command at {}", frontTalon.getSelectedSensorPosition(0));
    frontTalon.set(PercentOutput, STOP_OUTPUT);
    rearTalon.set(PercentOutput, STOP_OUTPUT);
  }

  @Override
  public void register(TelemetryService telemetryService) {
    telemetryService.register(new TalonItem(frontTalon, "Lift Front (" + FRONT_ID + ")"));
    telemetryService.register(new TalonItem(rearTalon, "Lift Rear (" + REAR_ID + ")"));
  }
}
