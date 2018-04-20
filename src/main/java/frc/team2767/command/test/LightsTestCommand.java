package frc.team2767.command.test;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Robot;
import frc.team2767.subsystem.vision.VisionSubsystem;

public class LightsTestCommand extends Command {

  private final VisionSubsystem visionSubsystem = Robot.INJECTOR.visionSubsystem();

  public LightsTestCommand() {
    requires(visionSubsystem);
    setRunWhenDisabled(true);
  }

  @Override
  protected void initialize() {
    visionSubsystem.enableLights(true);
  }

  @Override
  protected void end() {
    visionSubsystem.enableLights(false);
  }

  @Override
  protected boolean isFinished() {
    return false;
  }
}
