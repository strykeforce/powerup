package frc.team2767.command.sequence;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;
import frc.team2767.command.intake.IntakeEject;
import frc.team2767.command.intake.IntakeOpen;
import frc.team2767.command.shoulder.ShoulderPosition;

public class DropCube extends CommandGroup {

    public DropCube() {
        addSequential(new IntakeOpen());
        addSequential(new WaitCommand(0.5));
        addSequential(new ShoulderPosition(ShoulderPosition.Position.STOW));
    }
}
