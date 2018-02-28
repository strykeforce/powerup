package frc.team2767.command.auton;

import frc.team2767.command.StartPosition;
import openrio.powerup.MatchData.OwnedSide;

public interface OwnedSidesSettable {

  void setOwnedSide(StartPosition startPosition, OwnedSide nearSwitch, OwnedSide scale);
}
