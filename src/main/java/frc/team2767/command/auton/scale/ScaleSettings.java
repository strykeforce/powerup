package frc.team2767.command.auton.scale;

import com.moandjiezana.toml.Toml;
import frc.team2767.Robot;
import frc.team2767.command.StartPosition;

public class ScaleSettings {

  public final static ScaleSettings LEFT = getInstance(frc.team2767.command.StartPosition.LEFT);
  public final static ScaleSettings RIGHT = getInstance(frc.team2767.command.StartPosition.RIGHT);

  public static final String TABLE = Robot.TABLE + ".SCALESETTINGS";

  private final String Path1;
  private final String Path2;
  private final String Path3;
  private final int Distance;
  private final StartPosition StartPosition;
  private final int DriveStopDistance;
  private final int IntakeStopDistance;

  private final double Azimuth1;
  private final double Azimuth2;
  private final double Azimuth3;
  private final double Drive1;
  private final double Strafe1;

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

    this.Path1 = path1;
    this.Path2 = path2;
    this.Path3 = path3;
    this.Distance = distance;
    this.StartPosition = startPosition;
    this.IntakeStopDistance = intakeStopDistance;
    this.DriveStopDistance = driveStopDistance;

    this.Azimuth1 = azimuth1;
    this.Azimuth2 = azimuth2;
    this.Azimuth3 = azimuth3;

    this.Drive1 = drive1;
    this.Strafe1 = strafe1;
  }

  private static ScaleSettings getInstance(StartPosition startPosition) {
    Toml toml = (startPosition == frc.team2767.command.StartPosition.RIGHT ? Robot.INJECTOR.settings().getTable(TABLE
            .concat(".RIGHT")) : Robot.INJECTOR.settings().getTable(TABLE.concat(".LEFT")));

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

  public String getPath1() {
    return Path1;
  }

  public String getPath2() {
    return Path2;
  }

  public String getPath3() {
    return Path3;
  }

  public int getDistance() {
    return Distance;
  }

  public StartPosition getStartPosition() {
    return StartPosition;
  }

  public int getDriveStopDistance() {
    return DriveStopDistance;
  }

  public int getIntakeStopDistance() {
    return IntakeStopDistance;
  }

  public double getAzimuth1() {
    return Azimuth1;
  }

  public double getAzimuth2() {
    return Azimuth2;
  }

  public double getAzimuth3() {
    return Azimuth3;
  }

  public double getDrive1() {
    return Drive1;
  }

  public double getStrafe1() {
    return Strafe1;
  }
}
