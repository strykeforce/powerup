package frc.team2767.command.health;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class TestRunner extends CommandGroup {

  public TestRunner() {
    super("Health Check");
    addSequential(new SetUp());
    addSequential(new TestSuite());
    addSequential(new TearDown());
  }
}
