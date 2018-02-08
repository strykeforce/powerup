package frc.team2767;

import com.moandjiezana.toml.Toml;
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
  private static final String DEFAULTS = "/META-INF/powerup/settings.toml";

  private final Toml toml;

  @Inject
  public Settings(File config) {
    if (Files.notExists(config.toPath())) {
      logger.error("{} is missing, using defaults in " + DEFAULTS, config);
      toml = defaults();
      return;
    }

    this.toml = new Toml(defaults()).read(config);
    logger.info("reading settings from {}", config);
  }

  public Toml getTable(String key) {
    if (!toml.contains(key)) {
      logger.error("table with key '{}' not present", key);
      return new Toml();
    }
    return toml.getTable(key);
  }

  public boolean isIsolatedTestMode() {
    Toml toml = getTable(TABLE);
    return toml.getBoolean("isolatedTestMode", false);
  }

  private Toml defaults() {
    InputStream in = this.getClass().getResourceAsStream(DEFAULTS);
    return new Toml().read(in);
  }
}
