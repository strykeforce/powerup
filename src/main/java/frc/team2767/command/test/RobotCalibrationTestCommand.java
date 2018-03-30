package frc.team2767.command.test;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2767.Robot;
import frc.team2767.command.auton.PathCommand;
import frc.team2767.command.auton.StartPosition;
import frc.team2767.subsystem.DriveSubsystem;

public class RobotCalibrationTestCommand extends Command {

  private final DriveSubsystem drive = Robot.INJECTOR.driveSubsystem();
  private SendableChooser<RobotCalStartPosition> calibrationTestCommands;

  public RobotCalibrationTestCommand() {
    initChoosers();
    requires(drive);
  }

  private void initChoosers() {
    calibrationTestCommands = new SendableChooser<>();

    calibrationTestCommands.addObject("Red Right", RobotCalStartPosition.RED_RIGHT);
    calibrationTestCommands.addObject("Red Left", RobotCalStartPosition.RED_LEFT);
    calibrationTestCommands.addObject("Blue Right", RobotCalStartPosition.BLUE_RIGHT);
    calibrationTestCommands.addObject("Blue Left", RobotCalStartPosition.BLUE_LEFT);

    SmartDashboard.putData("Test/RobotCalibration", calibrationTestCommands);
  }

  public Command getCommand() {
    switch (calibrationTestCommands.getSelected()) {
      case RED_RIGHT:
        drive.setAngleAdjustment(StartPosition.RIGHT);
        break;
      case RED_LEFT:
        drive.setAngleAdjustment(StartPosition.LEFT);
        break;

      case BLUE_RIGHT:
        drive.setAngleAdjustment(StartPosition.RIGHT);
        break;

      case BLUE_LEFT:
        drive.setAngleAdjustment(StartPosition.LEFT);
        break;
    }

    return new PathCommand("SameCalPath");
  }

  @Override
  protected boolean isFinished() {
    return false;
  }

  public enum RobotCalStartPosition {
    RED_RIGHT,
    RED_LEFT,
    BLUE_RIGHT,
    BLUE_LEFT
  }
}
