package frc.team2767.subsystem.health;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.google.auto.factory.AutoFactory;
import com.google.auto.factory.Provided;
import org.strykeforce.thirdcoast.talon.Talons;

@AutoFactory
public class FollowerVelocityTest extends VelocityTest {

  private final int masterId;

  public FollowerVelocityTest(String name, int masterId, @Provided Talons talons) {
    super(name, talons);
    this.masterId = masterId;
  }

  @Override
  void runTest(TestCase tc, TalonSRX talon, Result result) throws InterruptedException {
    TalonSRX master = talons.getTalon(masterId);
    for (int i = 0; i < iterations; i++) {
      result.velocity += master.getSelectedSensorVelocity(0);
      result.current += talon.getOutputCurrent();
      Thread.sleep(tc.duration / iterations);
    }
  }
}
