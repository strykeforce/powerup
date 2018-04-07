package frc.team2767.command.test;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Robot;
import frc.team2767.command.auton.StartPosition;
import frc.team2767.subsystem.vision.VisionSubsystem;

public class VisionTestCommand extends Command {
  private final VisionSubsystem visionSubsystem = Robot.INJECTOR.visionSubsystem();

  @Override
  protected void initialize() {
    visionSubsystem.find(StartPosition.RIGHT);
  }

  @Override
  protected boolean isFinished() {
    return visionSubsystem.isFinished();
  }
}
