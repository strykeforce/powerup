package frc.team2767;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import frc.team2767.control.AutonChooser;
import frc.team2767.control.Controls;
import frc.team2767.control.SimpleTrigger;
import frc.team2767.subsystem.DriveSubsystem;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;

public class Robot extends TimedRobot {

  public static final SingletonComponent INJECTOR;
  public static final String TABLE = "POWERUP";
  private static final Logger logger;

  static {
    URL thirdCoastConfig = Robot.class.getResource("/META-INF/powerup/thirdcoast.toml");
    INJECTOR = DaggerSingletonComponent.builder().thirdCoastConfig(thirdCoastConfig).build();
    logger = LoggerFactory.getLogger(Robot.class);
  }

  private AutonChooser autonChooser;
  private Controls controls;
  private Settings settings;
  private DriveSubsystem driveSubsystem;
  private SimpleTrigger alignWheelsButtons;
  private Scheduler scheduler;
  private Command autonCommand;
  private boolean autonDone;

  @Override
  public void robotInit() {
    settings = INJECTOR.settings();
    controls = INJECTOR.controls();
    autonChooser = INJECTOR.autonChooser();
    scheduler = Scheduler.getInstance();
    logger.info("INIT in {} mode", settings.isEvent() ? "EVENT" : "SAFE");

    alignWheelsButtons = controls.getDriverControls().getAlignWheelsButtons();
    driveSubsystem = INJECTOR.driveSubsystem();
    driveSubsystem.zeroAzimuthEncoders();

    if (settings.isCameraEnabled()) CameraServer.getInstance().startAutomaticCapture();

    LiveWindow.disableAllTelemetry();
    if (!settings.isEvent()) {
      logger.info("telemetry service enabled");
      TelemetryService telemetryService = INJECTOR.telemetryService();
      INJECTOR.graphables().forEach(g -> g.register(telemetryService));
      telemetryService.start();
    }
  }

  @Override
  public void disabledInit() {
    logger.info("DISABLED");
    if (autonDone) {
      autonChooser = null;
      autonCommand = null;
    } else {
      autonChooser.reset();
    }
    Logging.flushLogs();
  }

  @Override
  public void autonomousInit() {
    logger.info("AUTONOMOUS");
    autonCommand = autonChooser.getCommand();
    driveSubsystem.setAngleAdjustment(autonChooser.getStartPosition());
    autonDone = settings.isEvent();
    autonCommand.start();
  }

  @Override
  public void teleopInit() {
    logger.info("TELEOP");
    scheduler.removeAll();
    driveSubsystem.stop();
  }

  @Override
  public void robotPeriodic() {}

  @Override
  public void disabledPeriodic() {
    if (alignWheelsButtons != null && alignWheelsButtons.hasActivated()) {
      logger.debug("align wheels buttons have activated");
      driveSubsystem.alignWheelsToBar();
    }

    if (!autonDone) autonChooser.checkAutonSwitch();
  }

  @Override
  public void autonomousPeriodic() {
    scheduler.run();
  }

  @Override
  public void teleopPeriodic() {
    scheduler.run();
  }
}
