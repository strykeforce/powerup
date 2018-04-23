package frc.team2767.subsystem.vision;

import com.moandjiezana.toml.Toml;
import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.team2767.Settings;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class VisionSubsystem extends Subsystem implements Callable<Double> {

  static final String IMAGE_DIR = "/home/lvuser";
  static final Logger logger = LoggerFactory.getLogger(VisionSubsystem.class);
  private static final double FRAME_WIDTH = 320;
  private static final int FRAME_HEIGHT = 240;
  private static final int FOV_DEG = 30;
  private static final double FOV_DEG_PER_PIXEL = FOV_DEG / (FRAME_WIDTH / 2);
  private static final String TABLE = "POWERUP.VISION";
  private static final String BOTTOM_CAMERA = "Intake";
  private static final Scalar RED = new Scalar(50, 50, 255);
  private static final Scalar GREEN = new Scalar(50, 255, 50);
  private static final Scalar WHITE = new Scalar(255, 255, 255);
  private static final Scalar BLUE = new Scalar(255, 50, 50);
  private final boolean DEBUG;
  private final UsbCamera camera;
  private final DigitalOutput lightsOutput = new DigitalOutput(6);
  private final ExecutorService executorService;
  private final Mat frame = new Mat();
  private int exposure;
  private int brightness;
  private GripPipeline gripPipeline = new GripPipeline();
  private Future<Double> result;
  private Future<?> testResult;
  private List<VisionTestRun> testRuns;

  @Inject
  public VisionSubsystem(Settings settings) {
    Toml toml = settings.getTable(TABLE);
    DEBUG = toml.getBoolean("debug");
    CameraServer server = CameraServer.getInstance();
    camera = server.startAutomaticCapture(BOTTOM_CAMERA, toml.getString("intakeCameraPath"));
    exposure = toml.getLong("exposure").intValue();
    brightness = toml.getLong("brightness").intValue();
    camera.setBrightness(brightness);
    camera.setExposureManual(exposure);
    camera.setExposureHoldCurrent();
    camera.setResolution((int) (2.0 * FRAME_WIDTH), 2 * FRAME_HEIGHT);
    lightsOutput.set(true);
    CvSink video = CameraServer.getInstance().getVideo(camera);
    video.setEnabled(true);
    executorService = Executors.newSingleThreadExecutor();
    logger.debug("camera is connected = {}", camera.isConnected());
  }

  public void enableLights(boolean enable) {
    lightsOutput.set(!enable);
  }

  public void findCube() {
    result = executorService.submit(this);
  }

  @Override
  @SuppressWarnings("FutureReturnValueIgnored")
  public Double call() throws Exception {
    logger.trace("starting frame capture");
    CvSink video = CameraServer.getInstance().getVideo(camera);
    video.grabFrame(frame);
    gripPipeline.process(frame);

    ArrayList<MatOfPoint> contours = gripPipeline.filterContoursOutput();
    logger.debug("Number of contours = {} ", contours.size());

    double bottomX = 0;
    double bottomY = 0;
    double bottomAngle = 0;

    if (!contours.isEmpty()) {
      for (MatOfPoint contour : contours) {
        Point[] points = contour.toArray();
        for (Point point : points) {
          if (point.y > bottomY) {
            bottomY = point.y;
            bottomX = point.x;
          }
        }
      }
    }

    if (!contours.isEmpty()) bottomAngle = (bottomX - FRAME_WIDTH / 2.0) * FOV_DEG_PER_PIXEL;
    logger.debug("cube bottom point angle x = {}", bottomAngle);

    final double debugBottomX = bottomX;
    final double debugBottomAngle = bottomAngle;

    if (DEBUG)
      executorService.submit(
          () -> saveDebugImages(frame, contours, debugBottomX, debugBottomAngle));

    return bottomAngle;
  }

  private void saveDebugImages(
      Mat source, ArrayList<MatOfPoint> contours, double bottomX, double bottomAngle) {
    Mat threshold = gripPipeline.hsvThresholdOutput();
    Imgproc.cvtColor(threshold, threshold, Imgproc.COLOR_GRAY2RGB);
    if (!contours.isEmpty()) {
      Imgproc.drawContours(threshold, contours, -1, RED, 2);
    }
    Imgproc.line(threshold, new Point(bottomX, 0), new Point(bottomX, FRAME_HEIGHT), GREEN);
    Imgproc.line(
        threshold, new Point(FRAME_WIDTH / 2, 0), new Point(FRAME_WIDTH / 2, FRAME_HEIGHT), BLUE);
    Imgproc.putText(
        threshold,
        String.format("angle = %4.2f", bottomAngle),
        new Point(4, 230),
        Core.FONT_HERSHEY_COMPLEX_SMALL,
        0.75,
        WHITE);
    Imgcodecs.imwrite(IMAGE_DIR + "/threshold.jpg", threshold);
    Imgcodecs.imwrite(IMAGE_DIR + "/full.jpg", source);
  }

  public boolean isFinished() {
    return result.isDone();
  }

  public double getCenterAngle() {
    try {
      return result.get();
    } catch (InterruptedException | ExecutionException e) {
      logger.error("error getting center angle", e);
    }
    return 0.0;
  }

  // Vision Test
  public VisionTestRun newTestRun() {
    if (testRuns == null) testRuns = new ArrayList<>();
    VisionTestRun testRun = new VisionTestRun();
    testRuns.add(testRun);
    testRun.setCamera(camera);
    testRun.setBrightness(brightness);
    testRun.setExposure(exposure);
    testRun.setGripPipeline(gripPipeline);
    return testRun;
  }

  public void runTest(VisionTest test) {
    testResult = executorService.submit(test);
  }

  public boolean isTestFinished() {
    return testResult.isDone();
  }

  public void saveTestResults() {
    testResult =
        executorService.submit(
            () -> {
              HtmlReport report = new HtmlReport(testRuns);
              report.save();
              report.archive();
            });
  }

  public void setExposure(int exposure) {
    this.exposure = exposure;
    CvSink video = CameraServer.getInstance().getVideo(camera);
    video.setEnabled(false);
    camera.setExposureManual(exposure);
    video.setEnabled(true);
  }

  @Override
  protected void initDefaultCommand() {}
}
