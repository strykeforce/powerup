package frc.team2767.subsystem;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoSet;

@Module
public abstract class SubsystemModule {

  @Binds
  @IntoSet
  public abstract Graphable climberSubsystemGraphable(ClimberSubsystem climberSubsystem);

  @Binds
  @IntoSet
  public abstract Graphable driveSubsystemGraphable(DriveSubsystem driveSubsystem);

  @Binds
  @IntoSet
  public abstract Graphable intakeSubsystemGraphable(IntakeSubsystem intakeSubsystem);

  @Binds
  @IntoSet
  public abstract Graphable liftSubsystemGraphable(LiftSubsystem liftSubsystem);

  @Binds
  @IntoSet
  public abstract Graphable shoulderSubsystemGraphable(ShoulderSubsystem shoulderSubsystem);

  @Binds
  @IntoSet
  public abstract Positionable liftSubsystemPositionable(LiftSubsystem liftSubsystem);

  @Binds
  @IntoSet
  public abstract Positionable shoulderSubsystemPositionable(ShoulderSubsystem shoulderSubsystem);
}
