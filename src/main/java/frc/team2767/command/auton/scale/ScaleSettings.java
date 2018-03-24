package frc.team2767.command.auton.scale;

import com.moandjiezana.toml.Toml;
import frc.team2767.Robot;
import frc.team2767.Settings;
import frc.team2767.command.auton.StartPosition;
import openrio.powerup.MatchData;

public class ScaleSettings {

  public static final String TABLE = Robot.TABLE + ".SCALESETTINGS";
  public static final ScaleSettings LEFT = getInstance(StartPosition.LEFT);
  public static final ScaleSettings RIGHT = getInstance(StartPosition.RIGHT);

  public static final ScaleSettings RIGHTTOLEFT = getInstance(StartPosition.RIGHT);
  public static final ScaleSettings LEFTTORIGHT = getInstance(StartPosition.LEFT);

  private final String path1;
  private final String path2;
  private final String path3;
  private final int distance;
  private final StartPosition startPosition;
  private final int driveStopDistance;
  private final int intakeStopDistance;

  private final double azimuth1;
  private final double azimuth2;
  private final double azimuth3;
  private final double drive1;
  private final double strafe1;

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

    this.path1 = path1;
    this.path2 = path2;
    this.path3 = path3;
    this.distance = distance;
    this.startPosition = startPosition;
    this.intakeStopDistance = intakeStopDistance;
    this.driveStopDistance = driveStopDistance;

    this.azimuth1 = azimuth1;
    this.azimuth2 = azimuth2;
    this.azimuth3 = azimuth3;

    this.drive1 = drive1;
    this.strafe1 = strafe1;
  }

  private static ScaleSettings getInstance(StartPosition startPosition) {
    Settings settings = Robot.INJECTOR.settings();
    //    Toml toml =
    //            startPosition == StartPosition.RIGHT
    //                    ? settings.getTable(TABLE.concat(".RIGHT"))
    //                    : settings.getTable(TABLE.concat(".LEFT"));

    Toml toml = settings.getTable(TABLE);

    switch (MatchData.getOwnedSide(
        MatchData.GameFeature.SCALE)) { // FIXME: you don't know this yet!
      case LEFT:
        toml =
            (startPosition == StartPosition.LEFT
                ? settings.getTable(TABLE.concat(".LEFT"))
                : settings.getTable(TABLE.concat(".RIGHTTOLEFT")));
        break;
      case RIGHT:
        toml =
            (startPosition == StartPosition.LEFT)
                ? settings.getTable(TABLE.concat(".LEFTTORIGHT"))
                : settings.getTable(TABLE.concat(".RIGHT"));
        break;
      default:
    }

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
    return path1;
  }

  public String getPath2() {
    return path2;
  }

  public String getPath3() {
    return path3;
  }

  public int getDistance() {
    return distance;
  }

  public StartPosition getStartPosition() {
    return startPosition;
  }

  public int getDriveStopDistance() {
    return driveStopDistance;
  }

  public int getIntakeStopDistance() {
    return intakeStopDistance;
  }

  public double getAzimuth1() {
    return azimuth1;
  }

  public double getAzimuth2() {
    return azimuth2;
  }

  public double getAzimuth3() {
    return azimuth3;
  }

  public double getDrive1() {
    return drive1;
  }

  public double getStrafe1() {
    return strafe1;
  }
}
