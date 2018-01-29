package frc.team2767;

import dagger.BindsInstance;
import dagger.Component;
import frc.team2767.subsystem.DriveSubsystem;
import java.io.File;
import javax.inject.Singleton;
import org.strykeforce.thirdcoast.swerve.GraphableSwerveDriveModule;
import org.strykeforce.thirdcoast.swerve.GyroModule;
import org.strykeforce.thirdcoast.swerve.SwerveDrive;
import org.strykeforce.thirdcoast.swerve.WheelModule;
import org.strykeforce.thirdcoast.talon.TalonFactory;
import org.strykeforce.thirdcoast.talon.TalonProvisioner;
import org.strykeforce.thirdcoast.telemetry.NetworkModule;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;

/** This interface configures dependency injection for the Robot. */
@Singleton
@Component(
  modules = {
    NetworkModule.class,
    GyroModule.class,
    WheelModule.class,
    GraphableSwerveDriveModule.class,
  }
)
public interface SingletonComponent {

  DriveSubsystem driveSubsystem();

  Controls controls();

  SwerveDrive swerveDrive();

  TelemetryService telemetryService();

  TalonProvisioner talonProvisioner();

  TalonFactory talonFactory();

  Settings settings();

  @Component.Builder
  interface Builder {

    @BindsInstance
    Builder config(File config);

    SingletonComponent build();
  }
}
