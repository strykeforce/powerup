package frc.team2767.command;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.Controls;
import frc.team2767.Robot;
import frc.team2767.subsystem.DriveSubsystem;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ControlTestCommand extends Command {
  private final DriveSubsystem drive;
  private final Controls controls;

  private static final Logger logger = LoggerFactory.getLogger(DriveSubsystem.class);

  @Inject
  public ControlTestCommand() {
    drive = Robot.COMPONENT.driveSubsystem();
    controls = Robot.COMPONENT.controls();
    requires(drive);
  }

  private static double applyDeadband(double input) {
    return Math.abs(input) > 0.05 ? input : 0;
  }

  @Override
  protected void initialize() {
    drive.enableTeleOp(true);
  }

  @Override
  protected void execute() {
    double forward = applyDeadband(controls.getForward());
    double strafe = applyDeadband(controls.getStrafe());
    double azimuth = applyDeadband(controls.getAzimuth());
    double tuner = applyDeadband(controls.getTuner());
    double rightY = applyDeadband(controls.getDriverRightY());

    boolean resetButton = controls.getResetButton();
    boolean leftButton = controls.getLeftButton();
    boolean rightShoulder = controls.getRightShoulder();
    boolean leftShoulderUp = controls.getLeftShoulderUp();
    boolean leftShoulderDown = controls.getLeftShoulderDown();

    boolean leftTopLeft = controls.getDriverLeftTopLeft();
    boolean leftTopRight = controls.getDriverLeftTopRight();
    boolean rightTopLeft = controls.getDriverRightTopLeft();
    boolean rightTopRight = controls.getDriverRightTopRight();

    boolean centerLeftUp = controls.getDriverCenterTopLeftUp();
    boolean centerLeftDown = controls.getDriverCenterTopLeftDown();
    boolean centerRightUp = controls.getDriverCenterTopRightUp();
    boolean centerRightDown = controls.getDriverCenterTopRightDown();

    test("forward", forward);
    test("strafe", strafe);
    test("azimuth", azimuth);
    test("tuner", tuner);
    test("driverRightY", rightY);

    test("resetbutton", resetButton);
    test("leftButton", leftButton);
    test("rightshoulder", rightShoulder);
    test("leftShoulderUp", leftShoulderUp);
    test("leftShoulderDown", leftShoulderDown);

    test("leftTopLeft", leftTopLeft);
    test("leftTopRight", leftTopRight);
    test("rightTopLeft", rightTopLeft);
    test("rightTopRight", rightTopRight);

    test("centerLeftUp", centerLeftUp);
    test("centerLeftDwon", centerLeftDown);
    test("centerRightUp", centerRightUp);
    test("centerRightDown", centerRightDown);

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
