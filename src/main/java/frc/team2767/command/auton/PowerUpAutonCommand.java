package frc.team2767.command.auton;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.Robot;
import frc.team2767.command.LogCommand;
import frc.team2767.command.extender.ExtenderUp;
import frc.team2767.command.intake.StartIntakeHold;
import frc.team2767.command.lift.LiftZero;
import frc.team2767.command.shoulder.ShoulderZeroWithEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import openrio.powerup.MatchData.OwnedSide;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PowerUpAutonCommand extends CommandGroup implements OwnedSidesSettable {

  private static final Logger logger = LoggerFactory.getLogger(PowerUpAutonCommand.class);

  private final Map<Scenario, Sequence> scenarios = new HashMap<>();

  private final StartPosition startPosition;

  public PowerUpAutonCommand(StartPosition startPosition) {
    this.startPosition = startPosition;
    addSequential(
        new CommandGroup() {
          {
            addParallel(new LiftZero());
            addParallel(new ShoulderZeroWithEncoder());
            addParallel(new ExtenderUp());
            addParallel(new StartIntakeHold());
          }
        });
    Robot.INJECTOR.intakeSensorsSubsystem().setAutonCommand(this);
  }

  public void addScenario(
      OwnedSide nearSwitch,
      OwnedSide scale,
      PowerUpGameFeature cube1,
      PowerUpGameFeature cube2,
      PowerUpGameFeature cube3) {
    Scenario scenario = new Scenario(nearSwitch, scale);
    Sequence sequence =
        new Sequence(
            getCube1Deliver(scenario, cube1),
            getCube2Fetch(scenario, cube1, cube2),
            getCube2Deliver(scenario, cube2),
            getCube3Fetch(scenario, cube2, cube3),
            getCube3Deliver(scenario, cube3));
    scenarios.put(scenario, sequence);
  }

  private Command getCube1Deliver(Scenario scenario, PowerUpGameFeature cube1) {
    Command command;
    switch (cube1) {
      case SWITCH:
        command = getSwitchCube1Deliver(scenario);
        break;
      case SCALE:
        command = getScaleCube1Deliver(scenario);
        break;
      case COOPERATIVE:
        command = new ScaleOppositeCube1DeliverDelay(startPosition);
        break;
      default:
        command = command = new LogCommand("Cube1Deliver game feature is NONE for " + scenario);
    }
    logger.debug("cube 1 deliver = {}", command);
    return command;
  }

  private Command getSwitchCube1Deliver(Scenario scenario) {
    if (scenario.isSwitchSameSide(startPosition)) return new SwitchSameCube1Deliver(startPosition);
    else return new SwitchOppositeCube1Deliver(startPosition);
  }

  private Command getScaleCube1Deliver(Scenario scenario) {
    if (scenario.isScaleSameSide(startPosition)) return new ScaleSameCube1Deliver(startPosition);
    else return new ScaleOppositeCube1Deliver(startPosition);
  }

  private Command getCube2Fetch(
      Scenario scenario, PowerUpGameFeature cube1, PowerUpGameFeature cube2) {
    Command command;
    switch (cube2) {
      case NONE:
        command = new LogCommand("Cube2Fetch game feature is NONE for " + scenario);
        break;
      case COOPERATIVE:
        command = new ScaleClear(startPosition);
        break;
      default:
        command = new Cube2Fetch(startPosition, cube1);
    }
    logger.debug("cube 2 fetch = {}", command);
    return command;
  }

  private Command getCube2Deliver(Scenario scenario, PowerUpGameFeature cube2) {
    Command command;
    switch (cube2) {
      case SWITCH:
        command = new SwitchCube2Deliver(startPosition);
        break;
      case SCALE:
        command = new ScaleCube2Deliver(startPosition);
        break;
      default:
        command = new LogCommand("Cube2Deliver game feature is NONE for " + scenario);
    }
    logger.debug("cube 2 deliver = {}", command);
    return command;
  }

  private Command getCube3Fetch(
      Scenario scenario, PowerUpGameFeature cube2, PowerUpGameFeature cube3) {
    Command command =
        cube3 == PowerUpGameFeature.NONE
            ? new LogCommand("Cube3Fetch game feature is NONE for " + scenario)
            : new Cube3Fetch(startPosition, cube2);
    logger.debug("cube 3 fetch = {}", command);
    return command;
  }

  private Command getCube3Deliver(Scenario scenario, PowerUpGameFeature cube3) {
    Command command;
    switch (cube3) {
      case SWITCH:
        command = new SwitchCube3Deliver(startPosition);
        break;
      case SCALE:
        command = new ScaleCube3Deliver(startPosition);
        break;
      default:
        command = new LogCommand("Cube3Deliver game feature is NONE for " + scenario);
    }
    logger.debug("cube 3 deliver = {}", command);
    return command;
  }

  @Override
  public void setOwnedSide(StartPosition startPosition, OwnedSide nearSwitch, OwnedSide scale) {
    Scenario scenario = new Scenario(nearSwitch, scale);
    Sequence sequence = scenarios.get(scenario);
    sequence.setOwnedSide(startPosition, nearSwitch, scale);
    addSequential(sequence.cube1Deliver);
    addSequential(sequence.cube2Fetch);
    addSequential(sequence.cube2Deliver);
    addSequential(sequence.cube3Fetch);
    addSequential(sequence.cube3Deliver);
    logger.info("configured {} for {}", sequence, scenario);
  }

  static final class Scenario {
    private final OwnedSide nearSwitch;
    private final OwnedSide scale;

    Scenario(OwnedSide nearSwitch, OwnedSide scale) {
      this.nearSwitch = nearSwitch;
      this.scale = scale;
    }

    boolean isSwitchSameSide(StartPosition startPosition) {
      return isSameSide(nearSwitch, startPosition);
    }

    boolean isScaleSameSide(StartPosition startPosition) {
      return isSameSide(scale, startPosition);
    }

    boolean isSameSide(OwnedSide gameFeature, StartPosition startPosition) {
      return (startPosition == StartPosition.LEFT && gameFeature == OwnedSide.LEFT)
          || (startPosition == StartPosition.RIGHT && gameFeature == OwnedSide.RIGHT);
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

    @Override
    public String toString() {
      return "Scenario{" + "nearSwitch=" + nearSwitch + ", scale=" + scale + '}';
    }
  }

  static final class Sequence implements OwnedSidesSettable {
    final Command cube1Deliver;
    final Command cube2Fetch;
    final Command cube2Deliver;
    final Command cube3Fetch;
    final Command cube3Deliver;

    Sequence(
        Command cube1Deliver,
        Command cube2Fetch,
        Command cube2Deliver,
        Command cube3Fetch,
        Command cube3Deliver) {
      this.cube1Deliver = cube1Deliver;
      this.cube2Fetch = cube2Fetch;
      this.cube2Deliver = cube2Deliver;
      this.cube3Fetch = cube3Fetch;
      this.cube3Deliver = cube3Deliver;
    }

    @Override
    public void setOwnedSide(StartPosition startPosition, OwnedSide nearSwitch, OwnedSide scale) {
      if (cube2Fetch instanceof OwnedSidesSettable)
        ((OwnedSidesSettable) cube2Fetch).setOwnedSide(startPosition, nearSwitch, scale);
      if (cube2Deliver instanceof OwnedSidesSettable)
        ((OwnedSidesSettable) cube2Deliver).setOwnedSide(startPosition, nearSwitch, scale);
      if (cube3Fetch instanceof OwnedSidesSettable)
        ((OwnedSidesSettable) cube3Fetch).setOwnedSide(startPosition, nearSwitch, scale);
      if (cube3Deliver instanceof OwnedSidesSettable)
        ((OwnedSidesSettable) cube3Deliver).setOwnedSide(startPosition, nearSwitch, scale);
    }

    @Override
    public String toString() {
      return "Sequence{"
          + "cube1Deliver="
          + cube1Deliver
          + ", cube2Fetch="
          + cube2Fetch
          + ", cube2Deliver="
          + cube2Deliver
          + ", cube3Fetch="
          + cube3Fetch
          + ", cube3Deliver="
          + cube3Deliver
          + '}';
    }
  }
}
