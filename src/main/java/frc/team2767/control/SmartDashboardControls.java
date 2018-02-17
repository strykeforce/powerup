package frc.team2767.control;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2767.Settings;
import frc.team2767.command.intake.IntakeEject;
import frc.team2767.command.intake.IntakeLoad;
import frc.team2767.command.lift.SaveZero;
import frc.team2767.command.lift.Zero;
import frc.team2767.command.shoulder.LoadParameters;
import frc.team2767.command.shoulder.ShoulderOpenLoopDown;
import frc.team2767.command.shoulder.ShoulderOpenLoopUp;
import frc.team2767.command.shoulder.ShoulderPosition;
import frc.team2767.command.shoulder.ShoulderStop;
import frc.team2767.command.shoulder.ShoulderZero;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SmartDashboardControls {

  @Inject
  public SmartDashboardControls(Settings settings) {
    Controls.logger.debug("initializing SmartDashboard controls");
    if (settings.isIsolatedTestMode()) return;

    SmartDashboard.putData("Intake/Driver", new IntakeLoad());
    SmartDashboard.putData("Intake/Eject", new IntakeEject());

    SmartDashboard.putData("Shoulder/LoadParametersCommand", new LoadParameters());
    SmartDashboard.putData("Shoulder/PositionCommand", new ShoulderPosition(6000));
    SmartDashboard.putData("Shoulder/Zero", new ShoulderZero());
    SmartDashboard.putData("Lift/SaveZero", new SaveZero());
    SmartDashboard.putData("Lift/Zero", new Zero());

    SmartDashboard.putData("Shoulder/Up", new ShoulderOpenLoopUp());
    SmartDashboard.putData("Shoulder/Down", new ShoulderOpenLoopDown());
    SmartDashboard.putData("Shoulder/Stop", new ShoulderStop());
  }
}
