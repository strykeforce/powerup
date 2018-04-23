package frc.team2767.control;

import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.buttons.Trigger;
import frc.team2767.Settings;
import frc.team2767.command.vision.VisionTestSuite;
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
      SmartDashboardControls smartDashboardControls,
      Settings settings) {

    this.autonSwitch = autonSwitch;
    this.driverControls = driverControls;

    if (!settings.isEvent()) {
      Trigger userButton =
          new Trigger() {
            @Override
            public boolean get() {
              return RobotController.getUserButton();
            }
          };
      userButton.whenActive(new VisionTestSuite());
    }
  }

  public DriverControls getDriverControls() {
    return driverControls;
  }

  public int getAutonomousSwitchPosition() {
    return autonSwitch.position();
  }
}
