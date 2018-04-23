package frc.team2767.command.vision;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Robot;
import frc.team2767.subsystem.vision.VisionSubsystem;
import frc.team2767.subsystem.vision.VisionTest;

class VisionTestCase extends Command {

  private final VisionSubsystem visionSubsystem = Robot.INJECTOR.visionSubsystem();
  private final VisionTest test;

  VisionTestCase(VisionTest test) {
    this.test = test;
    requires(visionSubsystem);
    setRunWhenDisabled(true);
  }

  @Override
  protected void initialize() {
    visionSubsystem.runTest(test);
  }

  @Override
  protected boolean isFinished() {
    return visionSubsystem.isTestFinished();
  }
}
