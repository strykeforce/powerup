package frc.team2767.control;

import edu.wpi.first.wpilibj.DigitalInput;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Accesses driver control input. */
@Singleton
public class Controls {

  static final Logger logger = LoggerFactory.getLogger(Controls.class);

  private final AutonSwitch autonSwitch;
  private final DriverControls driverControls;
  private final boolean jumperRemoved;

  @SuppressWarnings("unused")
  @Inject
  Controls(
      AutonSwitch autonSwitch,
      DriverControls driverControls,
      PowerUpControls powerUpControls,
      SmartDashboardControls smartDashboardControls) {

    this.autonSwitch = autonSwitch;
    this.driverControls = driverControls;

    DigitalInput di = new DigitalInput(9);
    jumperRemoved = di.get();
    di.free();
  }

  public DriverControls getDriverControls() {
    return driverControls;
  }

  public int getAutonomousSwitchPosition() {
    return autonSwitch.position();
  }

  public boolean isEvent() {
    return jumperRemoved;
  }
}
