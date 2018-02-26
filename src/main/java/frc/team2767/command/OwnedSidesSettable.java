package frc.team2767.command;

import openrio.powerup.MatchData.OwnedSide;

public interface OwnedSidesSettable {

  void setOwnedSide(StartPosition startPosition, OwnedSide nearSwitch, OwnedSide scale);
}
