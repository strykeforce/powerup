package frc.team2767.subsystem;

import static com.ctre.phoenix.motorcontrol.ControlMode.PercentOutput;
import static com.ctre.phoenix.motorcontrol.ControlMode.Velocity;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import java.util.function.DoubleConsumer;
import org.strykeforce.thirdcoast.swerve.SwerveDrive.DriveMode;
import org.strykeforce.thirdcoast.swerve.Wheel;
import org.strykeforce.thirdcoast.talon.Talons;
import org.strykeforce.thirdcoast.util.Settings;

public class PowerUpWheel extends Wheel {

  private final double AZIMUTH_SETPOINT_MAX = 10_000.0;

  private final DoubleConsumer openLoopDriver;
  private final DoubleConsumer closedLoopDriver;
  private final DoubleConsumer azimuthDriver;

  public PowerUpWheel(Settings settings, TalonSRX azimuth, TalonSRX drive) {
    super(settings, azimuth, drive);
    currentDriver = openLoopDriver = (setpoint) -> driveTalon.set(PercentOutput, setpoint);
    closedLoopDriver = (setpoint) -> driveTalon.set(Velocity, setpoint * kDriveSetpointMax);
    azimuthDriver = (setpoint) -> driveTalon.set(Velocity, setpoint * AZIMUTH_SETPOINT_MAX);
  }

  public PowerUpWheel(Talons talons, Settings settings, int index) {
    this(settings, talons.getTalon(index), talons.getTalon(index + 10));
  }

  @Override
  public void setDriveMode(DriveMode driveMode) {
    switch (driveMode) {
      case OPEN_LOOP:
      case TELEOP:
        currentDriver = openLoopDriver;
        break;
      case CLOSED_LOOP:
      case TRAJECTORY:
        driveTalon.selectProfileSlot(0, 0);
        currentDriver = closedLoopDriver;
        break;
      case AZIMUTH:
        driveTalon.selectProfileSlot(1, 0);
        currentDriver = azimuthDriver;
        break;
    }
  }
}
