package frc.team2767.subsystem.vision;

import static j2html.TagCreator.a;
import static j2html.TagCreator.body;
import static j2html.TagCreator.div;
import static j2html.TagCreator.each;
import static j2html.TagCreator.h1;
import static j2html.TagCreator.h2;
import static j2html.TagCreator.h3;
import static j2html.TagCreator.head;
import static j2html.TagCreator.hr;
import static j2html.TagCreator.img;
import static j2html.TagCreator.p;
import static j2html.TagCreator.styleWithInlineFile;
import static j2html.TagCreator.title;
import static java.nio.charset.StandardCharsets.UTF_8;

import j2html.TagCreator;
import j2html.tags.Tag;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.slf4j.Logger;

class HtmlReport {
  private static final Logger logger = VisionSubsystem.logger;
  private static final String DIR = "/home/lvuser/vision/";
  private static final String FORMAT = "%4.0f";
  private static final File FILE = new File(DIR + "index.html");
  private final Date date = new Date();

  private final List<VisionTestRun> testRuns;

  public HtmlReport(List<VisionTestRun> testRuns) {
    this.testRuns = testRuns;
  }

  private static Tag testRun(VisionTestRun tr) {
    return div(
        hr(),
        h2(String.format("Test Run %d", tr.getRun() + 1)),
        each(
            tr.tests,
            t ->
                div(
                    h3(t.getName()),
                    img().withSrc("full-" + t.getRun() + ".jpg"),
                    img().withSrc("threshold-" + t.getRun() + ".jpg"),
                    info(t))));
  }

  private static Tag info(VisionTest t) {
    return p(
        String.format(
            "exposure = %d, brightness = %d, hue = %3.0f - %3.0f, sat = %3.0f - %3.0f, val = %3.0f - %3.0f",
            t.exposure,
            t.brightness,
            t.hueLow,
            t.hueHigh,
            t.saturationLow,
            t.saturationHigh,
            t.valueLow,
            t.valueHigh));
  }

  private void writeToZipFile(String path, ZipOutputStream zipStream) throws IOException {

    logger.debug("writing file : {} to zip file", path);

    File aFile = new File(DIR + path);
    FileInputStream fis = new FileInputStream(aFile);
    ZipEntry zipEntry = new ZipEntry("report/" + timeStamp() + "/" + path);
    zipStream.putNextEntry(zipEntry);

    byte[] bytes = new byte[1024];
    int length;
    while ((length = fis.read(bytes)) >= 0) {
      zipStream.write(bytes, 0, length);
    }

    zipStream.closeEntry();
    fis.close();
  }

  private String timeStamp() {
    DateFormat dateFormat = new SimpleDateFormat("MMdd-HHmmss");
    return dateFormat.format(date);
  }

  void save() {
    logger.info("saving vision calibration report to {}", FILE);
    try (Writer writer = Files.newBufferedWriter(FILE.toPath(), UTF_8)) {
      DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      TagCreator.html(
              head(
                  title("Vision Calibration"),
                  styleWithInlineFile("/META-INF/powerup/healthcheck.css")),
              body(h1("POWER UP Vision Calibration"), p(dateFormat.format(date))),
              p(a("Download Report").withHref("report-" + timeStamp() + ".zip")),
              each(testRuns, HtmlReport::testRun))
          .render(writer);
    } catch (Exception e) {
      logger.error("can't write HTML report", e);
    }
  }

  void archive() {
    try (FileOutputStream fos = new FileOutputStream(DIR + "report-" + timeStamp() + ".zip")) {
      try (ZipOutputStream zos = new ZipOutputStream(fos)) {

        writeToZipFile("index.html", zos);
        for (VisionTestRun testRun : testRuns)
          for (VisionTest test : testRun.tests) {
            writeToZipFile("full-" + test.getRun() + ".jpg", zos);
            writeToZipFile("threshold-" + test.getRun() + ".jpg", zos);
          }
      }
    } catch (IOException e) {
      logger.error("can't write HTML report archive", e);
    }
  }
}
