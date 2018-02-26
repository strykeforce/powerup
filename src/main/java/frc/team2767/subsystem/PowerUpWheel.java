package frc.team2767.subsystem;

import static com.ctre.phoenix.motorcontrol.ControlMode.PercentOutput;
import static com.ctre.phoenix.motorcontrol.ControlMode.Velocity;
import static frc.team2767.subsystem.PowerUpWheel.State.*;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import java.util.function.DoubleConsumer;
import org.strykeforce.thirdcoast.swerve.SwerveDrive.DriveMode;
import org.strykeforce.thirdcoast.swerve.Wheel;
import org.strykeforce.thirdcoast.talon.Talons;
import org.strykeforce.thirdcoast.util.Settings;

public class PowerUpWheel extends Wheel {

  private final DoubleConsumer openLoopDriver;
  private final DoubleConsumer closedLoopDriver;
  private State state;

  public PowerUpWheel(Settings settings, TalonSRX azimuth, TalonSRX drive) {
    super(settings, azimuth, drive);
    currentDriver = openLoopDriver = (setpoint) -> driveTalon.set(PercentOutput, setpoint);
    closedLoopDriver = closedLoopDriver();
  }

  /**
   * Convenience constructor for a wheel by specifying the swerve driveTalon wheel number (0-3).
   *
   * @param talons the TalonFactory used to create Talons
   * @param settings the settings from TOML config file
   * @param index the wheel number
   */
  public PowerUpWheel(Talons talons, Settings settings, int index) {
    this(settings, talons.getTalon(index), talons.getTalon(index + 10));
  }

  /**
   * Set the drive mode
   *
   * @param driveMode the drive mode
   */
  @Override
  public void setDriveMode(DriveMode driveMode) {
    switch (driveMode) {
      case OPEN_LOOP:
      case TELEOP:
        currentDriver = openLoopDriver;
        break;
      case CLOSED_LOOP:
      case TRAJECTORY:
      case AZIMUTH:
        currentDriver = closedLoopDriver;
        state = INIT;
        break;
    }
  }

  /**
   * Implement the closed-loop drive strategy.
   *
   * @return the closed-loop driver.
   */
  private DoubleConsumer closedLoopDriver() {
    return (setpoint) -> {
      int output = (int) (setpoint * kDriveSetpointMax);
      int magnitude = Math.abs(output);
      State prev = state;

      // We assume smooth transitions through states, starting with LOW. If not, for example jumping
      // from LOW to high speed, we will apply mid-range tuning for 1 control-loop iteration.
      switch (state) {
        case INIT:
          state = LOW;
          break;
        case LOW:
          if (magnitude > LOW.max) state = MID;
          break;
        case MID:
          if (magnitude > MID.max) state = HIGH;
          else if (magnitude < MID.min) state = LOW;
          break;
        case HIGH:
          if (magnitude < HIGH.min) state = MID;
          break;
      }

      if (state != prev) driveTalon.selectProfileSlot(state.slot, 0);
      driveTalon.set(Velocity, output);
    };
  }

  enum State {
    INIT(0, 0, 0),
    LOW(0, 500, 3000),
    MID(1, 2500, 6000),
    HIGH(2, 5000, 40_000);

    final int slot;
    final int min, max;

    State(int slot, int min, int max) {
      this.slot = slot;
      this.min = min;
      this.max = max;
    }
  }
}
