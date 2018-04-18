package frc.team2767.command.auton;

import edu.wpi.first.wpilibj.command.CommandGroup;
import openrio.powerup.MatchData;

public class ScaleCube3Deliver extends CommandGroup implements OwnedSidesSettable {
  ScaleCube3Deliver(StartPosition startPosition) {}

  @Override
  public void setOwnedSide(
      StartPosition startPosition, MatchData.OwnedSide nearSwitch, MatchData.OwnedSide scale) {}
}
