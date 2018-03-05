package frc.team2767;

import com.moandjiezana.toml.Toml;
import edu.wpi.first.wpilibj.DigitalInput;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class Settings {

  public static final String TABLE = "POWERUP";
  private static final Logger logger = LoggerFactory.getLogger(Settings.class);
  private static final File CONFIG = new File("/home/lvuser/powerup.toml");
  private static final String DEFAULTS = "/META-INF/powerup/settings.toml";
  private static final String PATHS = "/META-INF/powerup/paths/";

  private final Toml toml;
  private final Toml defaults;
  private final boolean jumperRemoved;

  @Inject
  public Settings() {
    defaults = defaults();
    if (Files.exists(CONFIG.toPath())) {
      this.toml = new Toml(defaults()).read(CONFIG);
      System.out.println("POWER UP settings: " + CONFIG);
    } else {
      System.out.println("POWER UP settings file is missing: " + CONFIG);
      toml = defaults;
    }

    System.out.println("POWER UP default settings: jar:" + DEFAULTS);

    DigitalInput di = new DigitalInput(9);
    jumperRemoved = di.get();
    di.free();
  }

  public boolean isEvent() {
    return jumperRemoved;
  }

  public boolean isCameraEnabled() {
    return getTable(TABLE).getBoolean("enableCamera", true);
  }

  public Toml getTable(String key) {
    if (!toml.contains(key)) {
      logger.error("table with key '{}' not present", key);
      return new Toml();
    }
    Toml table = toml.getTable(key);
    Toml defaultTable = defaults.getTable(key);

    return new Toml(defaultTable).read(table);
  }

  public Toml getPath(String name) {
    InputStream in = this.getClass().getResourceAsStream(PATHS + name + ".toml");
    if (in == null) {
      logger.error("path '{}{}{}' not found", PATHS, name, ".toml");
      return null;
    }
    return new Toml().read(in);
  }

  private Toml defaults() {
    InputStream in = this.getClass().getResourceAsStream(DEFAULTS);
    return new Toml().read(in);
  }
}
