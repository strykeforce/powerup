package frc.team2767.control;

import com.moandjiezana.toml.Toml;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2767.Settings;
import frc.team2767.command.auton.AzimuthCommand;
import frc.team2767.command.climber.ClimberRelease;
import frc.team2767.command.climber.ClimberStop;
import frc.team2767.command.climber.ClimberUnwind;
import frc.team2767.command.drive.*;
import frc.team2767.command.extender.ExtenderDown;
import frc.team2767.command.extender.ExtenderUp;
import frc.team2767.command.health.HealthCheck;
import frc.team2767.command.intake.IntakeClose;
import frc.team2767.command.intake.IntakeOpen;
import frc.team2767.command.lift.LiftZero;
import frc.team2767.command.shoulder.ShoulderZero;
import frc.team2767.command.test.PathTestCommand;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SmartDashboardControls {

  @Inject
  public SmartDashboardControls(Settings settings) {
    Controls.logger.debug("initializing SmartDashboard controls");
    Toml toml = settings.getTable("POWERUP.SMARTDASHBOARD");

    if (toml.getBoolean("enablePit")) addPitCommands();
    if (toml.getBoolean("enableTest")) addTestCommands();
  }

  private void addPitCommands() {
    addWheelPitCommands();
    SmartDashboard.putData("Pit/Climber/Unwind", new ClimberUnwind());
    SmartDashboard.putData("Pit/Climber/Stop", new ClimberStop());
    SmartDashboard.putData("Pit/Climber/Release", new ClimberRelease());
    SmartDashboard.putData("Pit/Lift/Zero", new LiftZero());
    SmartDashboard.putData("Pit/Shoulder/Zero", new ShoulderZero());
  }

  private void addWheelPitCommands() {
    SmartDashboard.putData("Pit/Wheels/Reset", new ResetWheelAlignment());
    SmartDashboard.putData("Pit/Wheels/LockZero", new LockAzimuthPosition("Lock 0", 0));
    SmartDashboard.putData("Pit/Wheels/Lock2000", new LockAzimuthPosition("Lock 2000", 2000));
    SmartDashboard.putData("Pit/Wheels/UnlockZero", new UnlockAzimuthPosition());
    SmartDashboard.putData("Pit/Wheels/Inc/0", new AdjustWheelAlignment("Wheel 0 CCW", 0, 1));
    SmartDashboard.putData("Pit/Wheels/Dec/0", new AdjustWheelAlignment("Wheel 0 CW", 0, -1));
    SmartDashboard.putData("Pit/Wheels/Inc/1", new AdjustWheelAlignment("Wheel 1 CCW", 1, 1));
    SmartDashboard.putData("Pit/Wheels/Dec/1", new AdjustWheelAlignment("Wheel 1 CW", 1, -1));
    SmartDashboard.putData("Pit/Wheels/Inc/2", new AdjustWheelAlignment("Wheel 2 CCW", 2, 1));
    SmartDashboard.putData("Pit/Wheels/Dec/2", new AdjustWheelAlignment("Wheel 2 CW", 2, -1));
    SmartDashboard.putData("Pit/Wheels/Inc/3", new AdjustWheelAlignment("Wheel 3 CCW", 3, 1));
    SmartDashboard.putData("Pit/Wheels/Dec/3", new AdjustWheelAlignment("Wheel 3 CW", 3, -1));
  }

  private void addTestCommands() {
    SmartDashboard.putData("Test/HealthCheck", new HealthCheck());
    SmartDashboard.putData("Test/IntakeOpen", new IntakeOpen());
    SmartDashboard.putData("Test/IntakeClose", new IntakeClose());
    SmartDashboard.putData("Test/DriveZero", new DriveZero("Forward", 0.5));
    SmartDashboard.putData("Test/DriveZeroBackwards", new DriveZero("Reverse", -0.5));
    SmartDashboard.putData("Test/Azimuth", new AzimuthCommand(0));
    SmartDashboard.putData(
        "Test/Path/StraightLine", new PathTestCommand("Straight Line", "straight_line"));
    SmartDashboard.putData("Test/ExtenderUp", new ExtenderUp());
    SmartDashboard.putData("Test/ExtenderDown", new ExtenderDown());
  }
}
