package frc.team2767.command.auton.scale;

import com.moandjiezana.toml.Toml;
import frc.team2767.Robot;
import frc.team2767.Settings;
import frc.team2767.command.StartPosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class ScaleSettings {

  public static final String TABLE = Robot.TABLE + ".SCALESETTINGS";
  private static final Logger logger = LoggerFactory.getLogger(Settings.class);
  private static final File CONFIG = new File("/home/lvuser/powerup.toml");
  private static final Settings SETTINGS = new Settings();

  private final String kPath1;
  private final String kPath2;
  private final String kPath3;
  private final int kDistance;
  private final StartPosition kStartPosition;
  private final int kDriveStopDistance;
  private final int kIntakeStopDistance;

  private final double kAzimuth1;
  private final double kAzimuth2;
  private final double kAzimuth3;
  private final double kDrive1;
  private final double kStrafe1;

  private ScaleSettings(
      String path1,
      String path2,
      String path3,
      int distance,
      StartPosition startPosition,
      int intakeStopDistance,
      int driveStopDistance,
      double azimuth1,
      double azimuth2,
      double azimuth3,
      double drive1,
      double strafe1) {

    this.kPath1 = path1;
    this.kPath2 = path2;
    this.kPath3 = path3;
    this.kDistance = distance;
    this.kStartPosition = startPosition;
    this.kIntakeStopDistance = intakeStopDistance;
    this.kDriveStopDistance = driveStopDistance;

    this.kAzimuth1 = azimuth1;
    this.kAzimuth2 = azimuth2;
    this.kAzimuth3 = azimuth3;

    this.kDrive1 = drive1;
    this.kStrafe1 = strafe1;
  }

  public static ScaleSettings getInstance(StartPosition startPosition) {
    Toml toml;

    if (startPosition.equals(StartPosition.RIGHT)) {
      toml = Robot.INJECTOR.settings().getTable(TABLE.concat(".RIGHT"));
    } else toml = Robot.INJECTOR.settings().getTable(TABLE.concat(".LEFT"));

    return new ScaleSettings(
        toml.getString("path1"),
        toml.getString("path2"),
        toml.getString("path3"),
        toml.getLong("distance").intValue(),
        startPosition,
        toml.getLong("intakeStopDist").intValue(),
        toml.getLong("driveStopDist").intValue(),
        toml.getDouble("azimuth1"),
        toml.getDouble("azimuth2"),
        toml.getDouble("azimuth3"),
        toml.getDouble("drive1"),
        toml.getDouble("strafe1"));
  }

  public String getkPath1() {
    return kPath1;
  }

  public String getkPath2() {
    return kPath2;
  }

  public String getkPath3() {
    return kPath3;
  }

  public int getkDistance() {
    return kDistance;
  }

  public StartPosition getkStartPosition() {
    return kStartPosition;
  }

  public int getkDriveStopDistance() {
    return kDriveStopDistance;
  }

  public int getkIntakeStopDistance() {
    return kIntakeStopDistance;
  }

  public double getkAzimuth1() {
    return kAzimuth1;
  }

  public double getkAzimuth2() {
    return kAzimuth2;
  }

  public double getkAzimuth3() {
    return kAzimuth3;
  }

  public double getkDrive1() {
    return kDrive1;
  }

  public double getkStrafe1() {
    return kStrafe1;
  }
}
