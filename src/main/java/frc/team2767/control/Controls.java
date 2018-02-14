package frc.team2767.control;

import frc.team2767.Settings;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Accesses driver control input. */
@Singleton
public class Controls {

  private static final Logger logger = LoggerFactory.getLogger(Controls.class);

  private final AutonSwitch autonSwitch;
  private final DriverControls driverControls;
  private final PowerUpControls powerUpControls;
  private final SmartDashboardControls smartDashboardControls;

  @Inject
  Controls(
      Settings settings,
      AutonSwitch autonSwitch,
      DriverControls driverControls,
      PowerUpControls powerUpControls,
      SmartDashboardControls smartDashboardControls) {
    if (settings.isIsolatedTestMode()) logger.info("initializing controls in isolated test mode");

    this.autonSwitch = autonSwitch;
    this.driverControls = driverControls;
    this.powerUpControls = powerUpControls;
    this.smartDashboardControls = smartDashboardControls;
  }

  public DriverControls getDriverControls() {
    return driverControls;
  }

  public int getAutonomousSwitchPosition() {
    return autonSwitch.position();
  }
}
