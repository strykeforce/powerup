package frc.team2767.control;

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

  @SuppressWarnings("unused")
  @Inject
  Controls(
      AutonSwitch autonSwitch,
      DriverControls driverControls,
      PowerUpControls powerUpControls,
      SmartDashboardControls smartDashboardControls) {

    this.autonSwitch = autonSwitch;
    this.driverControls = driverControls;
  }

  public DriverControls getDriverControls() {
    return driverControls;
  }

  public int getAutonomousSwitchPosition() {
    return autonSwitch.position();
  }
}
