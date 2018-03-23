package frc.team2767.command.auton;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.command.LogCommand;
import frc.team2767.command.StartPosition;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import openrio.powerup.MatchData.GameFeature;
import openrio.powerup.MatchData.OwnedSide;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PowerUpAutonCommand extends PowerUpCommandGroup implements OwnedSidesSettable {

  private static final Logger logger = LoggerFactory.getLogger(PowerUpAutonCommand.class);

  private final StartPosition startPosition;
  private final GameFeature cube1;
  private final boolean crossSwitch;
  private final boolean crossScale;
  private final GameFeature cube2;

  private final Map<Key, Command> cube1Map = new HashMap<>();

  private final Command cube1Deliver;
  private final Command cube2Fetch;
  private final Command cube2Deliver;

  private PowerUpAutonCommand(
      StartPosition startPosition,
      GameFeature cube1,
      boolean crossSwitch,
      boolean crossScale,
      GameFeature cube2) {
    this.startPosition = Objects.requireNonNull(startPosition);
    this.cube1 = Objects.requireNonNull(cube1);
    this.cube2 = Objects.requireNonNull(cube2);
    this.crossSwitch = crossSwitch;
    this.crossScale = crossScale;

    initCube1DeliverCommand();

    //    cube1Deliver = new ScaleSameCube1Deliver(startPosition);
    cube1Deliver = new ScaleOppositeCube1Deliver(startPosition);
    cube2Fetch = new Cube2Fetch(startPosition, GameFeature.SCALE);
    //    cube2Deliver = new ScaleSameCube2Deliver(startPosition);
    //    cube2Fetch = new LogCommand("new Cube2Fetch(startPosition, GameFeature.SCALE)");
    cube2Deliver = new LogCommand("new ScaleSameCube2Deliver(startPosition)");
  }

  public static Builder builder() {
    return new Builder();
  }

  private void initCube1DeliverCommand() {
    switch (cube1) {
      case SWITCH_NEAR:
        initSwitchCube1DeliverCommand();
        break;
      case SCALE:
        initScaleCube1DeliverCommand();
        break;
      case SWITCH_FAR:
        throw new AssertionError();
    }
  }

  private void initSwitchCube1DeliverCommand() {
    switch (startPosition) {
      case LEFT:
        cube1Map.put(
            new Key(OwnedSide.LEFT, GameFeature.SWITCH_NEAR),
            new SwitchSameCube1Deliver(startPosition));
        if (crossSwitch)
          cube1Map.put(
              new Key(OwnedSide.RIGHT, GameFeature.SWITCH_NEAR),
              new SwitchOppositeCube1Deliver(startPosition));
        break;
      case RIGHT:
        cube1Map.put(
            new Key(OwnedSide.RIGHT, GameFeature.SWITCH_NEAR),
            new SwitchSameCube1Deliver(startPosition));
        if (crossSwitch)
          cube1Map.put(
              new Key(OwnedSide.LEFT, GameFeature.SWITCH_NEAR),
              new SwitchOppositeCube1Deliver(startPosition));
        break;
      default:
        throw new AssertionError();
    }
  }

  private void initScaleCube1DeliverCommand() {}

  @Override
  public void setOwnedSide(StartPosition startPosition, OwnedSide nearSwitch, OwnedSide scale) {
    addSequential(cube1Deliver);
    addSequential(cube2Fetch);
    addSequential(cube2Deliver);
  }

  public static class Builder {
    private StartPosition startPosition;
    private GameFeature cube1;
    private boolean crossSwitch = false;
    private boolean crossScale = false;
    private GameFeature cube2;

    private Builder() {}

    public PowerUpAutonCommand build() {
      return new PowerUpAutonCommand(startPosition, cube1, crossSwitch, crossScale, cube2);
    }

    public Builder startPosition(StartPosition startPosition) {
      this.startPosition = startPosition;
      return this;
    }

    public Builder cube1(GameFeature cube1) {
      this.cube1 = cube1;
      return this;
    }

    public Builder cube2(GameFeature cube2) {
      this.cube2 = cube2;
      return this;
    }

    public Builder crossForScale(boolean allowed) {
      crossScale = allowed;
      return this;
    }

    public Builder crossForSwitch(boolean allowed) {
      crossSwitch = allowed;
      return this;
    }

    private void validateGameFeature(GameFeature gameFeature) {
      if (gameFeature == GameFeature.SWITCH_FAR)
        throw new IllegalArgumentException("SWITCH_FAR not allowed");
    }
  }

  private static class Key {
    private final OwnedSide ownedSide;
    private final GameFeature gameFeature;

    public Key(OwnedSide ownedSide, GameFeature gameFeature) {
      this.ownedSide = ownedSide;
      this.gameFeature = gameFeature;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Key key = (Key) o;
      return ownedSide == key.ownedSide && gameFeature == key.gameFeature;
    }

    @Override
    public int hashCode() {
      return Objects.hash(ownedSide, gameFeature);
    }
  }
}
