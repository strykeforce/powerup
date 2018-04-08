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
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

public class Logging extends ContextAwareBase implements Configurator {

  private static final Level CONSOLE_LEVEL = Level.TRACE;
  private static final Level EVENT_LEVEL = Level.INFO;

  private static final String CONSOLE_NAME = "console";
  private static final String CONSOLE_PATTERN =
      "%-23(%d{HH:mm:ss.SSS} [%thread]) %highlight(%-5level) %logger{32} - %msg%n";

  private static final String FILE_NAME = "file";
  private static final String FILE_PATTERN =
      "%-23(%d{HH:mm:ss.SSS} [%thread]) %-5level %logger{32} - %msg%n";

  private static boolean immediateFlush;

  @NotNull
  private static Appender<ILoggingEvent> consoleAppender(LoggerContext lc) {
    ConsoleAppender<ILoggingEvent> ca = new ConsoleAppender<>();
    ca.setContext(lc);
    ca.setName(CONSOLE_NAME);
    LayoutWrappingEncoder<ILoggingEvent> encoder = getEncoder(lc, CONSOLE_PATTERN);
    ca.setEncoder(encoder);
    return ca;
  }

  @NotNull
  private static Appender<ILoggingEvent> fileAppender(LoggerContext lc) {
    FileAppender<ILoggingEvent> fa = new FileAppender<>();
    fa.setContext(lc);
    fa.setName(FILE_NAME);
    fa.setFile(getLogFile());
    fa.setImmediateFlush(immediateFlush);
    fa.setPrudent(false);
    LayoutWrappingEncoder<ILoggingEvent> encoder = getEncoder(lc, FILE_PATTERN);
    fa.setEncoder(encoder);
    return fa;
  }

  @NotNull
  private static String getLogFile() {
    DriverStation driverStation = DriverStation.getInstance();
    StringBuilder stringBuilder = new StringBuilder();

    Path path = FileSystems.getDefault().getPath("/media/sda1/logs/");
    if (Files.exists(path)) stringBuilder.append(path).append("/");
    else stringBuilder.append("/home/lvuser/logs/");

    DateFormat dateFormat = new SimpleDateFormat("yyMMddhhmm");
    stringBuilder.append(dateFormat.format(Calendar.getInstance().getTime()));

    DriverStation.MatchType matchType = driverStation.getMatchType();
    if (matchType != null) stringBuilder.append("-").append(matchType);
    stringBuilder.append(".log");

    String logFile = stringBuilder.toString();
    System.out.println("POWER UP log file: " + logFile);
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

  static void flushLogs() {
    Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    if (immediateFlush) {
      root.info("immediate flush is set, ignoring call to flushLogs");
      return;
    }
    root.info("flushing log file");

    FileAppender<ILoggingEvent> appender =
        (FileAppender<ILoggingEvent>) root.getAppender(FILE_NAME);
    if (appender == null) return;
    OutputStream os = appender.getOutputStream();
    try {
      os.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void configure(LoggerContext lc) {
    Settings settings = Robot.INJECTOR.settings();
    boolean event = settings.isEvent();
    immediateFlush = settings.getTable("POWERUP").getBoolean("immediateFlushLogFile", true);

    Appender<ILoggingEvent> appender;
    appender = event ? fileAppender(lc) : consoleAppender(lc);

    appender.start();

    Logger rootLogger = lc.getLogger(Logger.ROOT_LOGGER_NAME);
    rootLogger.addAppender(appender);
    rootLogger.setLevel(event ? EVENT_LEVEL : CONSOLE_LEVEL);
  }
}
