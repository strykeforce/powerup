package frc.team2767.subsystem.vision;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VisionTest implements Callable<Void> {

  static final String IMAGE_DIR = "/home/lvuser/vision";
  private static final Logger logger = LoggerFactory.getLogger(VisionTest.class);
  private static final Scalar RED = new Scalar(50, 50, 255);

  private static int runCount;
  final int exposure;
  final int brightness;
  private final int run;
  private final String name;
  double hueLow = -1.0;
  double hueHigh = -1.0;
  double saturationLow = -1.0;
  double saturationHigh = -1.0;
  double valueLow = -1.0;
  double valueHigh = -1.0;
  private UsbCamera camera;
  private GripPipeline gripPipeline;
  private Mat frame = new Mat();

  public VisionTest(
      String name, UsbCamera camera, int exposure, int brightness, GripPipeline gripPipeline) {
    this.name = name;
    this.exposure = exposure;
    this.brightness = brightness;
    this.camera = camera;
    this.gripPipeline = gripPipeline;
    run = runCount++;
  }

  @Override
  public Void call() throws Exception {
    if (hueLow != -1.0) gripPipeline.hsvThresholdHue[0] = hueLow;
    if (hueHigh != -1.0) gripPipeline.hsvThresholdHue[1] = hueHigh;
    if (saturationLow != -1.0) gripPipeline.hsvThresholdSaturation[0] = saturationLow;
    if (saturationHigh != -1.0) gripPipeline.hsvThresholdSaturation[1] = saturationHigh;
    if (valueLow != -1.0) gripPipeline.hsvThresholdValue[0] = valueLow;
    if (valueHigh != -1.0) gripPipeline.hsvThresholdValue[1] = valueHigh;

    hueLow = gripPipeline.hsvThresholdHue[0];
    hueHigh = gripPipeline.hsvThresholdHue[1];
    saturationLow = gripPipeline.hsvThresholdSaturation[0];
    saturationHigh = gripPipeline.hsvThresholdSaturation[1];
    valueLow = gripPipeline.hsvThresholdValue[0];
    valueHigh = gripPipeline.hsvThresholdValue[1];

    CvSink video = CameraServer.getInstance().getVideo(camera);
    video.grabFrame(frame);
    gripPipeline.process(frame);

    ArrayList<MatOfPoint> contours = gripPipeline.filterContoursOutput();

    Mat threshold = gripPipeline.hsvThresholdOutput();
    Imgproc.cvtColor(threshold, threshold, Imgproc.COLOR_GRAY2RGB);
    if (!contours.isEmpty()) Imgproc.drawContours(threshold, contours, -1, RED, 2);

    Imgcodecs.imwrite(IMAGE_DIR + "/threshold-" + run + ".jpg", threshold);
    Imgcodecs.imwrite(IMAGE_DIR + "/full-" + run + ".jpg", frame);

    return null;
  }

  int getRun() {
    return run;
  }

  public void setHueLow(double hueLow) {
    this.hueLow = hueLow;
  }

  public void setHueHigh(double hueHigh) {
    this.hueHigh = hueHigh;
  }

  public void setSaturationLow(double saturationLow) {
    this.saturationLow = saturationLow;
  }

  public void setSaturationHigh(double saturationHigh) {
    this.saturationHigh = saturationHigh;
  }

  public void setValueLow(double valueLow) {
    this.valueLow = valueLow;
  }

  public void setValueHigh(double valueHigh) {
    this.valueHigh = valueHigh;
  }

  public String getName() {
    return name;
  }
}
