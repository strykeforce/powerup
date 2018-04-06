package frc.team2767.subsystem.vision;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.vision.VisionThread;
import java.util.ArrayList;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class CameraSubsystem extends Subsystem implements Runnable {

  private static final int CAMERA_WIDTH = 320;
  private static final int FOV_DEG = 30;
  private static final int FOV_DEG_PER_PIXEL = FOV_DEG / (CAMERA_WIDTH / 2);

  private UsbCamera camera;
  private Rect candidateRect;
  private int rEdge;
  private int rRight;
  private double rCenter;
  private boolean validRead;
  private double xCoordinate;
  private double[] xArray = new double[5]; // potentially make length configurable
  private double cubeLeftAngle = -1;
  private double cubeRightAngle = -1;
  private double cubeCenterAngle = -1;

  public enum Side { // this currently says if you want the RIGHT or LEFT-most block.
    LEFT,
    RIGHT
  }

  public Side side;

  private static final Logger logger = LoggerFactory.getLogger(CameraSubsystem.class);

  private Thread thread;
  private volatile boolean running;

  @Inject
  public CameraSubsystem() {}

  public void find(Side side) {
    thread = new Thread(this);
    thread.start();
    running = true;
  }

  @Override
  public void run() {
    GripCode gripCode = new GripCode();
    CvSink pipeline = CameraServer.getInstance().getVideo();
    Mat source = new Mat();
    pipeline.grabFrame(source);
    gripCode.process(source);
    Mat threshold = gripCode.hsvThresholdOutput();
    Imgcodecs.imwrite("/home/lvuser/image.jpg", threshold);

    ArrayList<MatOfPoint> contours = gripCode.filterContoursOutput();
    logger.debug("Number of countours = {} ", contours.size());

    if (!contours.isEmpty()) { // if a contour is found
      if (side == Side.RIGHT) {
        rEdge = 0;
        for (MatOfPoint contour : contours) { // find the RIGHT-most contour
          Rect boundingRec = Imgproc.boundingRect(contour);
          if (boundingRec.x > rEdge) {
            candidateRect = boundingRec;
            rEdge = candidateRect.x + candidateRect.width;
          }
        }
      }
      if (side == Side.LEFT) {
        rEdge = CAMERA_WIDTH;
        for (MatOfPoint contour : contours) { // find the LEFT-most block
          if (Imgproc.boundingRect(contour).x < rEdge) {
            candidateRect = Imgproc.boundingRect(contour);
            rEdge = candidateRect.x;
            rCenter = candidateRect.x + (.5 * candidateRect.width);
            rRight = candidateRect.x + candidateRect.width;
          }
        }
      }
    }
    cubeLeftAngle = (rEdge - CAMERA_WIDTH / 2) * FOV_DEG_PER_PIXEL;
    cubeCenterAngle = (rCenter - CAMERA_WIDTH / 2) * FOV_DEG_PER_PIXEL;
    cubeRightAngle = (rRight - CAMERA_WIDTH / 2) * FOV_DEG_PER_PIXEL;
    logger.debug("cube LEFT edge angle = {}", cubeLeftAngle);
    logger.debug("cube RIGHT edge angle = {}", cubeRightAngle);
    logger.debug("cube CENTER edge angle = {}", cubeCenterAngle);
    running = false;
  }

  public boolean isFinished() {
    return (!running);
  }

  public void end() {
    try {
      thread.join();
    } catch (InterruptedException e) {
      logger.error("thread join", e);
    }
  }

  public void cameraInit() {
    System.out.println("Camera Init ran");
  }

  @Override
  protected void initDefaultCommand() {}

  public double getCubeLeftAngle() {
    return cubeLeftAngle;
  }

  public double getCubeRightAngle() {
    return cubeRightAngle;
  }

  public double getCubeCenterAngle() {
    return cubeCenterAngle;
  }
}
