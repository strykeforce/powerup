package frc.team2767.control;

import com.moandjiezana.toml.Toml;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2767.Settings;
import frc.team2767.command.auton.PathCommand;
import frc.team2767.command.climber.ClimberRelease;
import frc.team2767.command.climber.ClimberStop;
import frc.team2767.command.climber.ClimberUnwind;
import frc.team2767.command.drive.*;
import frc.team2767.command.health.TestRunner;
import frc.team2767.command.intake.IntakeIn;
import frc.team2767.command.intake.IntakeOut;
import frc.team2767.command.intake.IntakeStop;
import frc.team2767.command.lift.LiftDown;
import frc.team2767.command.lift.LiftStop;
import frc.team2767.command.lift.LiftUp;
import frc.team2767.command.lift.LiftZero;
import frc.team2767.command.shoulder.ShoulderZeroWithEncoder;
import frc.team2767.command.shoulder.ShoulderZeroWithLimitSwitch;
import frc.team2767.command.test.LidarTestCommand;
import frc.team2767.command.test.VisionTestCommand;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SmartDashboardControls {

  private final boolean isEvent;

  @Inject
  public SmartDashboardControls(Settings settings) {
    isEvent = settings.isEvent();
    Controls.logger.debug("initializing SmartDashboard controls");
    Toml toml = settings.getTable("POWERUP.SMARTDASHBOARD");

    if (toml.getBoolean("enablePit")) addPitCommands();
    if (toml.getBoolean("enableTest")) addTestCommands();
    if (toml.getBoolean("enableGame")) addGameCommands();
  }

  private void addPitCommands() {
    addWheelPitCommands();
    SmartDashboard.putData("Pit/Climber/Unwind", new ClimberUnwind());
    SmartDashboard.putData("Pit/Climber/Stop", new ClimberStop());
    SmartDashboard.putData("Pit/Climber/Release", new ClimberRelease());
    SmartDashboard.putData("Pit/Lift/Zero", new LiftZero());
    SmartDashboard.putData("Pit/Shoulder/ZeroLS", new ShoulderZeroWithLimitSwitch());
    SmartDashboard.putData("Pit/Shoulder/ZeroEnc", new ShoulderZeroWithEncoder());
    if (!isEvent) SmartDashboard.putData("Pit/HealthCheck", new TestRunner());
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
    SmartDashboard.putData("Test/DriveZero", new DriveZero("Forward", 0.5));
    SmartDashboard.putData("Test/DriveZeroBackwards", new DriveZero("Reverse", -0.5));
    SmartDashboard.putData("Test/Lidar", new LidarTestCommand());
    SmartDashboard.putData("Test/CarpetCalSame", new PathCommand("SameCalPath"));
    SmartDashboard.putData("Test/CarpetCalOpposite", new PathCommand("OppositeCalPath"));
<<<<<<< HEAD
=======
    SmartDashboard.putData("Test/VisionTest", new VisionTestCommand());
>>>>>>> pr/14
  }

  private void addGameCommands() {
    SmartDashboard.putData("Game/IntakeIn", new IntakeIn());
    SmartDashboard.putData("Game/IntakeStop", new IntakeStop());
    SmartDashboard.putData("Game/IntakeOut", new IntakeOut());
    SmartDashboard.putData("Game/ElevatorUp", new LiftUp());
    SmartDashboard.putData("Game/ElevatorStop", new LiftStop());
    SmartDashboard.putData("Game/ElevatorDown", new LiftDown());
  }
}
