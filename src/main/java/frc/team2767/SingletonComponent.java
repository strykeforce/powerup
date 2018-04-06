package frc.team2767;

import dagger.BindsInstance;
import dagger.Component;
import frc.team2767.control.AutonChooser;
import frc.team2767.control.Controls;
import frc.team2767.motion.PathControllerFactory;
import frc.team2767.subsystem.*;
import frc.team2767.subsystem.health.FollowerVelocityTestFactory;
import frc.team2767.subsystem.health.HealthCheckSubsystem;
import frc.team2767.subsystem.health.VelocityTestFactory;
import frc.team2767.subsystem.vision.CameraSubsystem;
import java.net.URL;
import java.util.Set;
import javax.inject.Singleton;
import org.strykeforce.thirdcoast.swerve.GyroModule;
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
    SubsystemModule.class,
    //    GraphableSwerveDriveModule.class,
  }
)
public interface SingletonComponent {

  Set<Graphable> graphables();

  ClimberSubsystem climberSubsystem();

  DriveSubsystem driveSubsystem();

  LiftSubsystem liftSubsystem();

  IntakeSubsystem intakeSubsystem();

  ShoulderSubsystem shoulderSubsystem();

  ExtenderSubsystem extenderSubsystem();

  CameraSubsystem cameraSubsystem();

  HealthCheckSubsystem healthCheckSubsystem();

  IntakeSensorsSubsystem intakeSensorsSubsystem();

  Controls controls();

  TelemetryService telemetryService();

  Talons talons();

  Settings settings();

  AutonChooser autonChooser();

  PathControllerFactory pathControllerFactory();

  VelocityTestFactory velocityTestFactory();

  FollowerVelocityTestFactory followerVelocityTestFactory();

  @Component.Builder
  interface Builder {

    @BindsInstance
    Builder thirdCoastConfig(URL config);

    SingletonComponent build();
  }
}
