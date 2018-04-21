package frc.team2767.command.test;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;
import frc.team2767.Robot;
import frc.team2767.command.auton.AzimuthToCube;
import frc.team2767.command.vision.LightsOff;
import frc.team2767.command.vision.LightsOn;
import frc.team2767.subsystem.vision.VisionSubsystem;

public class VisionTestCommand extends CommandGroup {
  private final VisionSubsystem visionSubsystem = Robot.INJECTOR.visionSubsystem();

  public VisionTestCommand() {
    setRunWhenDisabled(true);

    addSequential(new LightsOn());
    addSequential(new WaitCommand(0.1));

    addSequential(new AzimuthToCube());

    addSequential(new LightsOff());
  }
}
