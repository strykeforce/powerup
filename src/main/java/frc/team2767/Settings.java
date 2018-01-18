package frc.team2767;

import com.moandjiezana.toml.Toml;
import java.io.File;
import java.nio.file.Files;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class Settings {

  private static final Logger logger = LoggerFactory.getLogger(Settings.class);

  @Inject
  public Settings(File config) {
    if (Files.notExists(config.toPath())) {
      logger.error("{} is missing", config);
      return;
    }

    Toml toml = new Toml().read(config);
    logger.info("reading settings from {}", config);
  }
}
