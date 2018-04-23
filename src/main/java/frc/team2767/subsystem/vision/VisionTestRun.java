package frc.team2767.subsystem.vision;

import edu.wpi.cscore.UsbCamera;
import java.util.ArrayList;
import java.util.List;

public class VisionTestRun {

  private static int runCount;
  private final int run;
  List<VisionTest> tests = new ArrayList<>();
  private UsbCamera camera;
  private int exposure;
  private int brightness;
  private GripPipeline gripPipeline;

  public VisionTestRun() {
    run = runCount++;
  }

  public VisionTest newTest(String name) {
    VisionTest test = new VisionTest(name, camera, exposure, brightness, gripPipeline);
    tests.add(test);
    return test;
  }

  void setCamera(UsbCamera camera) {
    this.camera = camera;
  }

  public void setBrightness(int brightness) {
    this.brightness = brightness;
  }

  public void setExposure(int exposure) {
    this.exposure = exposure;
  }

  public void setGripPipeline(GripPipeline gripPipeline) {
    this.gripPipeline = gripPipeline;
  }

  public int getRun() {
    return run;
  }
}
