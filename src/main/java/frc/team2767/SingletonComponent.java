package frc.team2767;

import dagger.BindsInstance;
import dagger.Component;
import frc.team2767.control.AlignWheelsTrigger;
import frc.team2767.control.Controls;
import frc.team2767.motion.AzimuthControllerFactory;
import frc.team2767.motion.PathControllerFactory;
import frc.team2767.subsystem.*;
import java.net.URL;
import java.util.Set;
import javax.inject.Singleton;
import org.strykeforce.thirdcoast.swerve.GyroModule;
import org.strykeforce.thirdcoast.talon.Talons;
import org.strykeforce.thirdcoast.telemetry.NetworkModule;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;

/** This interface configures dependency injection for the Robot. */
@Singleton
@Component(
  modules = {
    NetworkModule.class,
    GyroModule.class,
    PowerUpWheelModule.class,
    SubsystemModule.class,
  }
)
public interface SingletonComponent {

  Set<Graphable> graphables();

  Set<Positionable> positionables();

  ClimberSubsystem climberSubsystem();

  DriveSubsystem driveSubsystem();

  LiftSubsystem liftSubsystem();

  IntakeSubsystem intakeSubsystem();

  ShoulderSubsystem shoulderSubsystem();

  Controls controls();

  TelemetryService telemetryService();

  Talons talons();

  Settings settings();

  AlignWheelsTrigger alignWheelsTrigger();

  PathControllerFactory pathControllerFactory();

  AzimuthControllerFactory azimuthControllerFactory();

  @Component.Builder
  interface Builder {

    @BindsInstance
    Builder thirdCoastConfig(URL config);

    SingletonComponent build();
  }
}
