package frc.team2767.command.auton;

import edu.wpi.first.wpilibj.command.CommandGroup;
import openrio.powerup.MatchData;

public class SwitchCube3Deliver extends CommandGroup implements OwnedSidesSettable {
  SwitchCube3Deliver(StartPosition startPosition) {}

  @Override
  public void setOwnedSide(
      StartPosition startPosition, MatchData.OwnedSide nearSwitch, MatchData.OwnedSide scale) {}
}
