package frc.team2767.command.vision;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;
import frc.team2767.Robot;
import frc.team2767.subsystem.vision.VisionSubsystem;
import frc.team2767.subsystem.vision.VisionTest;
import frc.team2767.subsystem.vision.VisionTestRun;
import java.util.Arrays;
import java.util.List;

public class VisionTestSuite extends CommandGroup {

  private final VisionSubsystem visionSubsystem = Robot.INJECTOR.visionSubsystem();

  public VisionTestSuite() {
    setRunWhenDisabled(true);

    addSequential(new LightsOn());

    addTestRun(20, Arrays.asList(60.0, 120.0, 180.0));
    addTestRun(40, Arrays.asList(60.0, 120.0, 180.0));
    addTestRun(240, Arrays.asList(60.0, 120.0, 180.0));

    addReport();

    addSequential(new LightsOff());
  }

  private void addTestRun(int exposure, List<Double> saturations) {
    addSequential(new VisionTestCameraSettings(exposure));
    addSequential(new WaitCommand(2));

    VisionTestRun testRun = visionSubsystem.newTestRun();
    testRun.setExposure(exposure);
    for (double sat : saturations) {
      VisionTest test =
          testRun.newTest(String.format("Exposure = %d Saturation = %3.0f", exposure, sat));
      test.setSaturationLow(sat);
      addSequential(new VisionTestCase(test));
    }
  }

  private void addReport() {
    addSequential(
        new Command() {
          {
            requires(visionSubsystem);
            setRunWhenDisabled(true);
          }

          @Override
          protected void initialize() {
            visionSubsystem.saveTestResults();
          }

          @Override
          protected boolean isFinished() {
            return visionSubsystem.isTestFinished();
          }
        });
  }
}
