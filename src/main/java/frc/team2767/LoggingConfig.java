package frc.team2767;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.Configurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ch.qos.logback.core.spi.ContextAwareBase;
import edu.wpi.first.wpilibj.DriverStation;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.jetbrains.annotations.NotNull;

public class LoggingConfig extends ContextAwareBase implements Configurator {

  private static final String CONSOLE_PATTERN =
      "%-23(%d{HH:mm:ss.SSS} [%thread]) %highlight(%-5level) %logger{32} - %msg%n";

  private static final String FILE_PATTERN =
      "%-23(%d{HH:mm:ss.SSS} [%thread]) %-5level %logger{32} - %msg%n";

  @NotNull
  private static Appender<ILoggingEvent> consoleAppender(LoggerContext lc) {
    ConsoleAppender<ILoggingEvent> ca = new ConsoleAppender<>();
    ca.setContext(lc);
    ca.setName("console");
    LayoutWrappingEncoder<ILoggingEvent> encoder = getEncoder(lc, CONSOLE_PATTERN);
    ca.setEncoder(encoder);
    return ca;
  }

  @NotNull
  private static Appender<ILoggingEvent> fileAppender(LoggerContext lc) {
    FileAppender<ILoggingEvent> fa = new FileAppender<>();
    fa.setContext(lc);
    fa.setName("file");
    fa.setFile(getLogFile());
    fa.setImmediateFlush(false);
    fa.setPrudent(false);
    LayoutWrappingEncoder<ILoggingEvent> encoder = getEncoder(lc, FILE_PATTERN);
    fa.setEncoder(encoder);
    return fa;
  }

  @NotNull
  private static String getLogFile() {
    DriverStation driverStation = DriverStation.getInstance();
    StringBuilder stringBuilder = new StringBuilder();

    Path path = FileSystems.getDefault().getPath("/media/sda/logs/");
    if (Files.exists(path)) stringBuilder.append(path);
    else stringBuilder.append("/home/lvuser/logs/");

    DateFormat dateFormat = new SimpleDateFormat("yymmddhhmm");
    stringBuilder.append(dateFormat.format(Calendar.getInstance().getTime()));

    DriverStation.MatchType matchType = driverStation.getMatchType();
    if (matchType != null) stringBuilder.append("-").append(matchType);
    stringBuilder.append(".log");

    String logFile = stringBuilder.toString();
    System.out.println("Logging to " + logFile);
    return logFile;
  }

  @NotNull
  private static LayoutWrappingEncoder<ILoggingEvent> getEncoder(LoggerContext lc, String pattern) {
    LayoutWrappingEncoder<ILoggingEvent> encoder = new LayoutWrappingEncoder<>();
    encoder.setContext(lc);

    PatternLayout layout = new PatternLayout();
    layout.setPattern(pattern);

    layout.setContext(lc);
    layout.start();
    encoder.setLayout(layout);
    return encoder;
  }

  @Override
  public void configure(LoggerContext lc) {
    System.out.println("Setting up robot logging configuration.");
    boolean fms = DriverStation.getInstance().isFMSAttached();

    Appender<ILoggingEvent> appender;
    appender = fms ? fileAppender(lc) : consoleAppender(lc);

    appender.start();

    Logger rootLogger = lc.getLogger(Logger.ROOT_LOGGER_NAME);
    rootLogger.addAppender(appender);
    rootLogger.setLevel(fms ? Level.INFO : Level.DEBUG);
  }
}
