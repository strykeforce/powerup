package frc.team2767.command.vision;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.Robot;
import frc.team2767.subsystem.vision.VisionSubsystem;

public class VisionTestCameraSettings extends InstantCommand {

  private final VisionSubsystem visionSubsystem = Robot.INJECTOR.visionSubsystem();

  private final int exposure;

  public VisionTestCameraSettings(int exposure) {
    this.exposure = exposure;
    requires(visionSubsystem);
    setRunWhenDisabled(true);
  }

  @Override
  protected void initialize() {
    visionSubsystem.setExposure(exposure);
  }
}
