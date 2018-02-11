package frc.team2767.subsystem;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoSet;

@Module
public abstract class SubsystemModule {

  @Binds
  @IntoSet
  public abstract Graphable climberSubsystem(ClimberSubsystem climberSubsystem);

  @Binds
  @IntoSet
  public abstract Graphable driveSubsystem(DriveSubsystem driveSubsystem);

  @Binds
  @IntoSet
  public abstract Graphable intakeSubsystem(IntakeSubsystem intakeSubsystem);

  @Binds
  @IntoSet
  public abstract Graphable liftSubsystem(LiftSubsystem liftSubsystem);

  @Binds
  @IntoSet
  public abstract Graphable shoulderSubsystem(ShoulderSubsystem shoulderSubsystem);
}
