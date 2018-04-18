package frc.team2767.command.test;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Robot;
import frc.team2767.subsystem.DriveSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MotionTestCommand extends Command {

  private static final Logger logger = LoggerFactory.getLogger(MotionTestCommand.class);

  private final DriveSubsystem driveSubsystem = Robot.INJECTOR.driveSubsystem();

  public MotionTestCommand() {
    requires(driveSubsystem);
  }

  @Override
  protected void initialize() {
    driveSubsystem.motionTo(-150, 100_000, 30);
  }

  @Override
  protected boolean isFinished() {
    return driveSubsystem.isMotionFinished();
  }

  @Override
  protected void end() {
    driveSubsystem.endMotion();
  }
}
