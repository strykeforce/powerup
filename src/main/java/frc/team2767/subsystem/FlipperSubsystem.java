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

@Singleton
public class FlipperSubsystem extends Subsystem implements Graphable {

  private static final int ID = 60; // PDP 8

  private static final double OUTPUT = 0.5;
  private static final double DOWN_OUTPUT = 0.2;
  private static final double STOP_OUTPUT = 0.0;

  private static final Logger logger = LoggerFactory.getLogger(FlipperSubsystem.class);

  private final TalonSRX talon;

  @Inject
  public FlipperSubsystem(Talons talons) {
    talon = talons.getTalon(ID);
    talon.setSelectedSensorPosition(0, 0, 10);
    logger.debug("Flipper init");
  }

  public void run() {
    logger.debug("flipping");
    talon.set(PercentOutput, OUTPUT);
  }

  public void reset() {
    logger.debug("resetting");
    talon.set(PercentOutput, -1 * DOWN_OUTPUT);
  }

  public void stop() {
    logger.debug("stopping");
    talon.set(PercentOutput, STOP_OUTPUT);
  }

  public boolean isFinishedUp() {
    return talon.getSelectedSensorPosition(0) > 1000;
  }

  public boolean isFinishedDown() {
    return talon.getSelectedSensorPosition(0) < 200;
  }

  @Override
  protected void initDefaultCommand() {}

  @Override
  public void register(TelemetryService telemetryService) {
    if (talon != null) telemetryService.register(talon);
  }
}
