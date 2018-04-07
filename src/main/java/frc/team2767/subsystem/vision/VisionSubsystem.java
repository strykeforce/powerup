package frc.team2767.subsystem.vision;

import com.moandjiezana.toml.Toml;
import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.team2767.Settings;
import frc.team2767.command.auton.StartPosition;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.*;

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

  private static final Logger logger = LoggerFactory.getLogger(VisionSubsystem.class);

  private final UsbCamera camera;
  private final ExecutorService executorService;
  private GripCode gripCode;

  private Future<Double> result;
  private StartPosition startPosition;
  double bottomY;
  double bottomX;
  double bottomAngle;

  public enum Side { // this currently says if you want the RIGHT or LEFT-most block.
    LEFT,
    RIGHT
  }

  private volatile boolean running;

  @Inject
  public VisionSubsystem(Settings settings) {
    Toml toml = settings.getTable(TABLE);
    CameraServer server = CameraServer.getInstance();
    camera = server.startAutomaticCapture(BOTTOM_CAMERA, toml.getString("intakeCameraPath"));
    camera.setBrightness(toml.getLong("brightness").intValue());
    camera.setExposureManual(toml.getLong("exposure").intValue());
    camera.setExposureHoldCurrent();
    camera.setResolution((int) FRAME_WIDTH, FRAME_HEIGHT);

    UsbCamera elevator = server.startAutomaticCapture(TOP_CAMERA, toml.getString("liftCameraPath"));
    elevator.setResolution((int) FRAME_WIDTH, FRAME_HEIGHT);

    executorService = Executors.newSingleThreadExecutor();
  }

  public void find(StartPosition startPosition) {
    bottomY = 0;
    bottomX = 0;

    if (gripCode == null) gripCode = new GripCode();
    this.startPosition = startPosition;
    result = executorService.submit(this);
  }

  @Override
  public Double call() throws Exception {
    logger.trace("starting frame capture");
    CvSink video = CameraServer.getInstance().getVideo(camera);
    Mat frame = new Mat();
    video.grabFrame(frame);
    gripCode.process(frame);

    ArrayList<MatOfPoint> contours = gripCode.filterContoursOutput();
    logger.debug("Number of countours = {} ", contours.size());

    double center = FRAME_WIDTH / 2;
    MatOfPoint bestContour = null;

    if (!contours.isEmpty()) { // if a contour is found
      if (startPosition == StartPosition.RIGHT) {
        double leftEdge = FRAME_WIDTH;
        for (MatOfPoint contour : contours) { // find the RIGHT-most contour
          Point[] points = contour.toArray();
          for (Point point : points) {
            if (point.y > bottomY) {
              bottomX = point.x;
              bottomY = point.y;
              bottomAngle = (bottomX - FRAME_WIDTH / 2.0) * FOV_DEG_PER_PIXEL;
            }
          }
          Rect boundingRec = Imgproc.boundingRect(contour);
          if (boundingRec.x < leftEdge) {
            bestContour = contour;
            leftEdge = boundingRec.x;
            center = boundingRec.x + boundingRec.width / 2;
          }
        }
      }
      if (startPosition == StartPosition.LEFT) {
        int rightEdge = 0;
        for (MatOfPoint contour : contours) {
          Point[] points = contour.toArray();
          for (Point point : points) {
            if (point.y > bottomY) {
              bottomX = point.x;
              bottomY = point.y;
              bottomAngle = (bottomX - FRAME_WIDTH / 2.0) * FOV_DEG_PER_PIXEL;
            }
          }
          Rect boundingRec = Imgproc.boundingRect(contour);
          int x = boundingRec.x + boundingRec.width; // find the LEFT-most block
          if (x > rightEdge) {
            bestContour = contour;
            rightEdge = x;
            center = x - boundingRec.width / 2;
          }
        }
      }
    }
    double cubeCenterAngle = (center - FRAME_WIDTH / 2.0) * FOV_DEG_PER_PIXEL;

    Mat threshold = gripCode.hsvThresholdOutput();
    Imgproc.cvtColor(threshold, threshold, Imgproc.COLOR_GRAY2RGB);
    if (bestContour != null) {
      Imgproc.drawContours(threshold, Collections.singletonList(bestContour), -1, RED, 2);
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
    Imgcodecs.imwrite("/home/lvuser/image.jpg", threshold);

    logger.debug("cube CENTER angle = {}", cubeCenterAngle);
    logger.debug("cube bottom point x = {}", bottomAngle);
    return bottomAngle;
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
}
