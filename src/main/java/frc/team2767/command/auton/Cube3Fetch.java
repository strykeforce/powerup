package frc.team2767.command.auton;

import edu.wpi.first.wpilibj.command.CommandGroup;
import openrio.powerup.MatchData;

public class Cube3Fetch extends CommandGroup implements OwnedSidesSettable {
  Cube3Fetch(StartPosition startPosition, PowerUpGameFeature cube2) {}

  @Override
  public void setOwnedSide(
      StartPosition startPosition, MatchData.OwnedSide nearSwitch, MatchData.OwnedSide scale) {}
}
