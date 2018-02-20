package frc.team2767.control;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2767.Settings;
import frc.team2767.command.climber.ClimberRelease;
import frc.team2767.command.climber.ClimberStop;
import frc.team2767.command.climber.ClimberUnwind;
import frc.team2767.command.lift.LiftSaveZero;
import frc.team2767.command.lift.Zero;
import frc.team2767.command.shoulder.ShoulderZero;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SmartDashboardControls {

  @Inject
  public SmartDashboardControls(Settings settings) {
    Controls.logger.debug("initializing SmartDashboard controls");
    if (settings.isIsolatedTestMode()) return;

    SmartDashboard.putData("Pit/Climber/Unwind", new ClimberUnwind());
    SmartDashboard.putData("Pit/Climber/Stop", new ClimberStop());
    SmartDashboard.putData("Pit/Climber/Release", new ClimberRelease());

    SmartDashboard.putData("Pit/Lift/SaveZero", new LiftSaveZero());
    SmartDashboard.putData("Pit/Lift/Zero", new Zero());

    SmartDashboard.putData("Pit/Shoulder/Zero", new ShoulderZero());

    //    SmartDashboard.putData("Test/Azimuth", new AzimuthCommand(0));
    //    SmartDashboard.putData(
    //        "Path/StraightLine", new PathTestCommand("Straight Line", "straight_line"));

  }
}
