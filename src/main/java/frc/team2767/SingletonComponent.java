package frc.team2767;

import dagger.BindsInstance;
import dagger.Component;
import frc.team2767.control.AlignWheelsTrigger;
import frc.team2767.control.Controls;
import frc.team2767.subsystem.*;
import java.io.File;
import java.net.URL;
import java.util.Set;
import javax.inject.Singleton;
import org.strykeforce.thirdcoast.swerve.GyroModule;
import org.strykeforce.thirdcoast.swerve.SwerveDrive;
import org.strykeforce.thirdcoast.swerve.WheelModule;
import org.strykeforce.thirdcoast.talon.Talons;
import org.strykeforce.thirdcoast.telemetry.NetworkModule;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;

/** This interface configures dependency injection for the Robot. */
@Singleton
@Component(
  modules = {
    NetworkModule.class,
    GyroModule.class,
    WheelModule.class,
    //    GraphableSwerveDriveModule.class,
    SubsystemModule.class,
  }
)
public interface SingletonComponent {

  Set<Graphable> graphables();

  ClimberSubsystem climberSubsystem();

  DriveSubsystem driveSubsystem();

  LiftSubsystem liftSubsystem();

  IntakeSubsystem intakeSubsystem();

  FlipperSubsystem flipperSubsystem();

  ShoulderSubsystem shoulderSubsystem();

  Controls controls();

  SwerveDrive swerveDrive();

  TelemetryService telemetryService();

  Talons talons();

  Settings settings();

  AlignWheelsTrigger alignWheelsTrigger();

  @Component.Builder
  interface Builder {

    @BindsInstance
    Builder thirdCoastConfig(URL config);

    @BindsInstance
    Builder robotConfig(File config);

    SingletonComponent build();
  }
}
