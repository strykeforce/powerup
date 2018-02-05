package frc.team2767;

import dagger.BindsInstance;
import dagger.Component;
import frc.team2767.subsystem.ClimberSubsystem;
import frc.team2767.subsystem.DriveSubsystem;
import frc.team2767.subsystem.Graphable;
import frc.team2767.subsystem.IntakeSubsystem;
import frc.team2767.subsystem.LiftSubsystem;
import frc.team2767.subsystem.SubsystemModule;
import frc.team2767.trigger.AlignWheelsTrigger;
import java.io.File;
import java.util.Set;
import javax.inject.Singleton;
import org.strykeforce.thirdcoast.swerve.GraphableSwerveDriveModule;
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
    GraphableSwerveDriveModule.class,
    SubsystemModule.class,
  }
)
public interface SingletonComponent {

  Set<Graphable> graphables();

  ClimberSubsystem climberSubsystem();

  DriveSubsystem driveSubsystem();

  LiftSubsystem liftSubsystem();

  IntakeSubsystem intakeSubsystem();

  Controls controls();

  SwerveDrive swerveDrive();

  TelemetryService telemetryService();

  Talons talons();

  Settings settings();

  AlignWheelsTrigger alignWheelsTrigger();

  @Component.Builder
  interface Builder {

    @BindsInstance
    Builder config(File config);

    SingletonComponent build();
  }
}
