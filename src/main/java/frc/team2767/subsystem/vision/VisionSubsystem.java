package frc.team2767.subsystem.vision;

import com.moandjiezana.toml.Toml;
import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.team2767.Settings;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.*;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class VisionSubsystem extends Subsystem implements Callable<Double> {

  private static final double FRAME_WIDTH = 320;
  private static final int FRAME_HEIGHT = 240;
  private static final int FOV_DEG = 30;
  private static final double FOV_DEG_PER_PIXEL = FOV_DEG / (FRAME_WIDTH / 2);
  private static final String TABLE = "POWERUP.VISION";
  private static final String BOTTOM_CAMERA = "Intake";
  private static final String TOP_CAMERA = "Elevator";
  private static final Scalar RED = new Scalar(50, 50, 255);
  private static final Scalar GREEN = new Scalar(50, 255, 50);
  private static final Scalar WHITE = new Scalar(255, 255, 255);
  private static final Scalar BLUE = new Scalar(255, 50, 50);
  private static final String IMAGE_DIR = "/home/lvuser/vision";

  private static final Logger logger = LoggerFactory.getLogger(VisionSubsystem.class);

  private final boolean DEBUG;

  private final UsbCamera camera;
  private final DigitalOutput lightsOutput = new DigitalOutput(6);
  private final ExecutorService executorService;
  private final Mat frame = new Mat();
  private GripPipeline gripPipeline = new GripPipeline();
  private Future<Double> result;
  private volatile boolean running;

  @Inject
  public VisionSubsystem(Settings settings) {
    Toml toml = settings.getTable(TABLE);
    DEBUG = toml.getBoolean("debug");
    CameraServer server = CameraServer.getInstance();
    camera = server.startAutomaticCapture(BOTTOM_CAMERA, toml.getString("intakeCameraPath"));
    camera.setBrightness(toml.getLong("brightness").intValue());
    camera.setExposureManual(toml.getLong("exposure").intValue());
    camera.setExposureHoldCurrent();
    camera.setResolution((int) (2.0 * FRAME_WIDTH), 2 * FRAME_HEIGHT);
    lightsOutput.set(true);
    executorService = Executors.newSingleThreadExecutor();
    if (DEBUG) new File(IMAGE_DIR).mkdir();
  }

  public void enableLights(boolean enable) {
    lightsOutput.set(!enable);
  }

  public void findCube() {
    result = executorService.submit(this);
  }

  @Override
  public Double call() throws Exception {
    logger.trace("starting frame capture");
    CvSink video = CameraServer.getInstance().getVideo(camera);
    video.grabFrame(frame);
    gripPipeline.process(frame);

    ArrayList<MatOfPoint> contours = gripPipeline.filterContoursOutput();
    logger.debug("Number of countours = {} ", contours.size());

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

    if (DEBUG) saveImages(frame, contours, bottomX, bottomAngle);

    logger.debug("cube bottom point angle x = {}", bottomAngle);
    return bottomAngle;
  }

  private void saveImages(
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
        new Point(4, 12),
        Core.FONT_HERSHEY_COMPLEX_SMALL,
        0.75,
        WHITE);
    Imgcodecs.imwrite(IMAGE_DIR + "/threshold.jpg", threshold);
    Imgcodecs.imwrite(IMAGE_DIR + "/resized.jpg", gripPipeline.resizeImageOutput());
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

  @Override
  protected void initDefaultCommand() {}

  public enum Side { // this currently says if you want the RIGHT or LEFT-most block.
    LEFT,
    RIGHT
  }
}
