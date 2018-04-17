package frc.team2767.command.test;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.ConditionalCommand;
import edu.wpi.first.wpilibj.command.InstantCommand;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2767.Robot;
import frc.team2767.command.LogCommand;
import frc.team2767.command.auton.PathCommand;
import frc.team2767.command.auton.StartPosition;
import frc.team2767.command.drive.ZeroGyroYawCommand;
import frc.team2767.subsystem.DriveSubsystem;

public class RobotCalibrationTestCommand extends CommandGroup {

  private final DriveSubsystem drive = Robot.INJECTOR.driveSubsystem();
  private SendableChooser<RobotCalStartPosition> calibrationTestCommands;
  private boolean sideSelected;

  public RobotCalibrationTestCommand() {
    initChoosers();

    addSequential(new ZeroGyroYawCommand());
    addSequential(
        new InstantCommand() {
          {
            requires(drive);
          }

          @Override
          protected void initialize() {
            RobotCalStartPosition side = calibrationTestCommands.getSelected();
            if (side == null) {
              sideSelected = false;
              return;
            }
            switch (side) {
              case BLUE_RIGHT:
              case RED_RIGHT:
                drive.setAngleAdjustment(StartPosition.RIGHT);
                break;
              case BLUE_LEFT:
              case RED_LEFT:
                drive.setAngleAdjustment(StartPosition.LEFT);
                break;
            }
            sideSelected = true;
          }
        });

    addSequential(
        new ConditionalCommand(
            new PathCommand("SameCalPath"), new LogCommand("Side not selected in SmartDashboard")) {
          @Override
          protected boolean condition() {
            return sideSelected;
          }
        });
  }

  private void initChoosers() {
    calibrationTestCommands = new SendableChooser<>();

    calibrationTestCommands.addObject("Red Right", RobotCalStartPosition.RED_RIGHT);
    calibrationTestCommands.addObject("Red Left", RobotCalStartPosition.RED_LEFT);
    calibrationTestCommands.addObject("Blue Right", RobotCalStartPosition.BLUE_RIGHT);
    calibrationTestCommands.addObject("Blue Left", RobotCalStartPosition.BLUE_LEFT);

    SmartDashboard.putData("Test/RobotCalibration", calibrationTestCommands);
  }

  public enum RobotCalStartPosition {
    RED_RIGHT,
    RED_LEFT,
    BLUE_RIGHT,
    BLUE_LEFT
  }
}
