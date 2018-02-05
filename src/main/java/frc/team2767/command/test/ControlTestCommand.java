package frc.team2767.command.test;

import static org.strykeforce.thirdcoast.swerve.SwerveDrive.DriveMode.OPEN_LOOP;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Robot;
import frc.team2767.control.Controls;
import frc.team2767.subsystem.DriveSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Tests button mapping and controls. */
public class ControlTestCommand extends Command {
  private static final Logger logger = LoggerFactory.getLogger(DriveSubsystem.class);
  private final DriveSubsystem drive;
  private final Controls controls;

  public ControlTestCommand() {
    drive = Robot.INJECTOR.driveSubsystem();
    controls = Robot.INJECTOR.controls();
    requires(drive);
  }

  private static double applyDeadband(double input) {
    return Math.abs(input) > 0.05 ? input : 0;
  }

  @Override
  protected void initialize() {
    drive.setDriveMode(OPEN_LOOP);
  }

  @Override
  protected void execute() {
    //    double forward = applyDeadband(controls.getForward());
    //    double strafe = applyDeadband(controls.getStrafe());
    //    double azimuth = applyDeadband(controls.getAzimuth());
    //    double tuner = applyDeadband(controls.getTuner());
    //    double rightY = applyDeadband(controls.getDriverRightY());
    //
    //    boolean resetButton = controls.getResetButton();
    //    boolean leftButton = controls.getLeftButton();
    //    boolean rightShoulder = controls.getRightShoulder();
    //    boolean leftShoulderUp = controls.getLeftShoulderUp();
    //    boolean leftShoulderDown = controls.getLeftShoulderDown();
    //
    //    boolean leftTrimXPos = controls.getDriverLeftTrimXPos();
    //    boolean leftTrimXNeg = controls.getDriverLeftTrimXNeg();
    //    boolean rightTrimXNeg = controls.getDriverRightTrimXNeg();
    //    boolean rightTrimXPos = controls.getDriverRightTrimXPos();
    //
    //    boolean leftTrimYPos = controls.getDriverLeftTrimYPos();
    //    boolean leftTrimYNeg = controls.getDriverLeftTrimYNeg();
    //    boolean rightTrimYPos = controls.getDriverRightTrimYPos();
    //    boolean rightTrimYNeg = controls.getDriverRightTrimYNeg();

    //    test("forward", forward);
    //    test("strafe", strafe);
    //    test("azimuth", azimuth);
    //    test("tuner", tuner);
    //    test("driverRightY", rightY);
    //
    //    test("resetbutton", resetButton);
    //    test("leftButton", leftButton);
    //    test("rightshoulder", rightShoulder);
    //    test("leftShoulderUp", leftShoulderUp);
    //    test("leftShoulderDown", leftShoulderDown);
    //
    //    test("leftTrimXNeg", leftTrimXNeg);
    //    test("leftTrimXPos", leftTrimXPos);
    //    test("rightTrimXNeg", rightTrimXNeg);
    //    test("rightTrimXPos", rightTrimXPos);
    //
    //    test("leftTrimYPos", leftTrimYPos);
    //    test("leftTrimYNeg", leftTrimYNeg);
    //    test("rightTrimYPos", rightTrimYPos);
    //    test("rightTrimYNeg", rightTrimYNeg);
  }

  private void test(String name, double a) {
    if (a != 0) {
      logger.debug("{}={}", name, a);
    }
  }

  private void test(String name, boolean a) {
    if (a) {
      logger.debug("{}={}", name, a);
    }
  }

  @Override
  protected boolean isFinished() {
    return false;
  }

  @Override
  protected void end() {
    drive.stop();
  }
}
