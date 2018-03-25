package frc.team2767.command.auton;

import java.util.Objects;
import openrio.powerup.MatchData;

final class Scenario {
  private final StartPosition startPosition;
  private final PowerUpGameFeature gameFeature;
  private final MatchData.OwnedSide ownedSide;

  Scenario(
      StartPosition startPosition, PowerUpGameFeature gameFeature, MatchData.OwnedSide ownedSide) {
    this.startPosition = startPosition;
    this.gameFeature = gameFeature;
    this.ownedSide = ownedSide;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Scenario scenario = (Scenario) o;
    return startPosition == scenario.startPosition
        && gameFeature == scenario.gameFeature
        && ownedSide == scenario.ownedSide;
  }

  @Override
  public int hashCode() {
    return Objects.hash(startPosition, gameFeature, ownedSide);
  }
}
