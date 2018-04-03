package frc.team2767.subsystem.vision;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.vision.VisionThread;
import frc.team2767.Settings;
import frc.team2767.subsystem.ExtenderSubsystem;
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
import org.strykeforce.thirdcoast.talon.Talons;

@Singleton
public class CameraSubsystem extends Subsystem implements Runnable {
  private VisionThread visionThread;
  private final Object imgLock = new Object();
  public UsbCamera camera;
  public Rect r;
  public int rEdge;
  public boolean validRead;
  public double xCoordinate;
  public double[] xArray = new double[5]; // potentially make length configurable
  public double cubeAngle = -1;

  public enum Side { // this currently says if you want the right or left-most block.
    left,
    right
  }

  public Side side;

  private static final Logger logger = LoggerFactory.getLogger(ExtenderSubsystem.class);

  private Thread thread;
  private volatile boolean cancelled;
  private volatile boolean running;

  @Inject
  public CameraSubsystem(Talons talons, Settings settings) {}

  public void initialize() {
    //side = Side.right;
    cancelled = false;
    thread = new Thread(this);
    thread.start();
  }

  public double findLeft(){
    side = Side.left;
    cancelled = false;
    thread = new Thread(this);
    thread.start();
    return cubeAngle;
  }

  public double findRight(){
    side = Side.right;
    cancelled = false;
    thread = new Thread(this);
    thread.start();
    return cubeAngle;
  }

  @Override
  public void run() {
    System.out.println("run ran");
    GripCode gripCode = new GripCode();
    CvSink pipeline = CameraServer.getInstance().getVideo();
    Mat source = new Mat();
    pipeline.grabFrame(source);
    gripCode.process(source);
    Mat threshold = gripCode.hsvThresholdOutput();
    Imgcodecs.imwrite("/home/lvuser/image.jpg", threshold);

    ArrayList<MatOfPoint> image = gripCode.filterContoursOutput();
    int m = image.size(); // find the number of contours
    System.out.println("size " + m);

    if (!image.isEmpty()) { // if a contour is found
      System.out.println("not empty ran");
      if (side == Side.right) {
        System.out.println("right side ran");
        rEdge = 0;
        for (int n = 0; n < m; n++) { // find the right-most contour
          if (Imgproc.boundingRect(image.get(n)).x > rEdge) {
            r = Imgproc.boundingRect(image.get(n));
            rEdge = r.x + r.width;
          }
        }
      }
      if (side == Side.left) {
        rEdge = 320;
        for (int n = 0; n < m; n++) { // find the left-most block
          if (Imgproc.boundingRect(image.get(n)).x < rEdge) {
            r = Imgproc.boundingRect(image.get(n));
            rEdge = r.x;
          }
        }
      }
    }
    cubeAngle = (rEdge - 160) * 30 / 160;
    System.out.println("Angle: " + cubeAngle);
    running = false;
}

  public boolean isFinished() {
    System.out.println("iFinished: " + !running);
    return (!running);
  }

  public boolean isCancelled() {
    System.out.println("isCancelled: " + cancelled);
    if (cancelled) {
      return true;
    } else {
      return false;
    }
  }

  public void end() {
    System.out.println("end ran");
    try {
      thread.join();
    } catch (InterruptedException e) {
      logger.error("thread join", e);
    }
  }

  public void cancel() {
    cancelled = true;
  }

  public void cameraInit() {

    System.out.println("Camera Init ran");
  }

  public double getAngle() {
    System.out.println("CameraAngle ran");
    /// *
    visionThread =
        new VisionThread(
            camera,
            new GripCode(),
            pipeline -> {
              int m = pipeline.filterContoursOutput().size(); // find the number of contours
              System.out.println("visionThread ran");
              if (!pipeline.filterContoursOutput().isEmpty()) { // if a contour is found
                synchronized (imgLock) {
                  System.out.println("synchronized ran");
                  if (side == Side.right) {
                    rEdge = 0;
                    for (int n = 0; n < m; n++) { // find the right-most contour
                      if (Imgproc.boundingRect(pipeline.filterContoursOutput().get(n)).x > rEdge) {
                        r = Imgproc.boundingRect(pipeline.filterContoursOutput().get(n));
                        rEdge = r.x + r.width;
                        System.out.println("Cube angle: " + rEdge);
                      }
                    }
                  }
                  if (side == Side.left) {
                    rEdge = 320;
                    for (int n = 0; n < m; n++) { // find the left-most block
                      if (Imgproc.boundingRect(pipeline.filterContoursOutput().get(n)).x < rEdge) {
                        r = Imgproc.boundingRect(pipeline.filterContoursOutput().get(n));
                        rEdge = r.x;
                      }
                    }
                  }
                }
              }
            });
    visionThread.start(); // */

    cubeAngle = (xCoordinate - 160) * 30 / 160;
    System.out.println(cubeAngle);
    return cubeAngle;
  }

  public void stopCapture() {
    visionThread.stop();
  }

  public boolean finishedRead() {
    return cubeAngle > -1;
  }

  @Override
  protected void initDefaultCommand() {}
}
