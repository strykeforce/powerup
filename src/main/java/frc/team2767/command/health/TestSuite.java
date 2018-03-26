package frc.team2767.command.health;

import edu.wpi.first.wpilibj.command.CommandGroup;

class TestSuite extends CommandGroup {

  TestSuite() {
    addSequential(new SwerveAzimuthTest());
    addSequential(new SwerveDriveTest());
    addSequential(new IntakeTest());
    addSequential(new ShoulderTest());
    addSequential(new LiftTest());
  }
}
