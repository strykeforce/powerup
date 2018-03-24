package frc.team2767.command.auton;

import edu.wpi.first.wpilibj.command.Command;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import openrio.powerup.MatchData.GameFeature;
import openrio.powerup.MatchData.OwnedSide;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PowerUpAutonCommand extends PowerUpCommandGroup implements OwnedSidesSettable {

  private static final Logger logger = LoggerFactory.getLogger(PowerUpAutonCommand.class);

  private final Map<Scenario, Sequence> scenarios = new HashMap<>();

  private final StartPosition startPosition;
  private final Sequence sequence;

  public PowerUpAutonCommand(StartPosition startPosition) {
    this.startPosition = startPosition;

    Sequence scaleOpposite =
        new Sequence(
            new ScaleOppositeCube1Deliver(startPosition),
            new Cube2Fetch(startPosition, GameFeature.SCALE),
            new ScaleCube2Deliver(startPosition));

    Sequence scaleSame =
        new Sequence(
            new ScaleSameCube1Deliver(startPosition),
            new Cube2Fetch(startPosition, GameFeature.SCALE),
            new ScaleCube2Deliver(startPosition));

    sequence = scaleOpposite;
  }

  public void addScenario(
      OwnedSide nearSwitch, OwnedSide scale, PowerUpGameFeature cube1, PowerUpGameFeature cube2) {
    Scenario scenario = new Scenario(nearSwitch, scale);
    Sequence sequence;
    switch (startPosition) {
      case UNKNOWN:
        break;
      case LEFT:
        break;
      case CENTER:
        break;
      case RIGHT:
        break;
    }
  }

  @Override
  public void setOwnedSide(StartPosition startPosition, OwnedSide nearSwitch, OwnedSide scale) {
    addSequential(sequence.cube1Deliver);
    addSequential(sequence.cube2Fetch);
    addSequential(sequence.cube2Deliver);
  }

  private static class Scenario {
    private final OwnedSide nearSwitch;
    private final OwnedSide scale;

    public Scenario(OwnedSide nearSwitch, OwnedSide scale) {
      this.nearSwitch = nearSwitch;
      this.scale = scale;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Scenario scenario = (Scenario) o;
      return nearSwitch == scenario.nearSwitch && scale == scenario.scale;
    }

    @Override
    public int hashCode() {
      return Objects.hash(nearSwitch, scale);
    }
  }

  private static class Sequence {
    private final Command cube1Deliver;
    private final Command cube2Fetch;
    private final Command cube2Deliver;

    public Sequence(Command cube1Deliver, Command cube2Fetch, Command cube2Deliver) {
      this.cube1Deliver = cube1Deliver;
      this.cube2Fetch = cube2Fetch;
      this.cube2Deliver = cube2Deliver;
    }
  }
}
