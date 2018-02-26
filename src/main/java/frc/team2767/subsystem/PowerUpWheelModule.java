package frc.team2767.subsystem;

import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import org.strykeforce.thirdcoast.swerve.Wheel;
import org.strykeforce.thirdcoast.talon.Talons;
import org.strykeforce.thirdcoast.util.Settings;

@Module
public abstract class PowerUpWheelModule {

  @Provides
  @Singleton
  public static Wheel[] provideWheels(Talons talons, Settings settings) {
    return new Wheel[] {
      new PowerUpWheel(talons, settings, 0), // front left
      new PowerUpWheel(talons, settings, 1), // front right
      new PowerUpWheel(talons, settings, 2), // rear left
      new PowerUpWheel(talons, settings, 3) // rear right
    };
  }
}
