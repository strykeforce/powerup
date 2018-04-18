package frc.team2767.command.test;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;
import frc.team2767.command.auton.DriveFromCube;
import frc.team2767.command.auton.DriveToCube;
import frc.team2767.command.intake.EnableLidar;

public class DriveToFromCubeCommand extends CommandGroup {

  public DriveToFromCubeCommand() {
    int targetDistance = 40;
    boolean left = true;
    boolean cross = false;

    addSequential(new EnableLidar());
    addSequential(new WaitCommand(0.3));
    DriveToCube driveToCube = new DriveToCube(targetDistance, left, cross);
    addSequential(driveToCube);
    addSequential(new DriveFromCube(driveToCube));
  }
}
