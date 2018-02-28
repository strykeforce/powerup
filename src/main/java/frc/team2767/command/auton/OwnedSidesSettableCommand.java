package frc.team2767.command.auton;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.ConditionalCommand;
import frc.team2767.command.StartPosition;
import java.util.ArrayList;
import java.util.List;
import openrio.powerup.MatchData.OwnedSide;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class OwnedSidesSettableCommand extends ConditionalCommand
    implements OwnedSidesSettable {

  static final Logger logger = LoggerFactory.getLogger(OwnedSidesSettable.class);

  StartPosition startPosition;
  OwnedSide nearSwitch;
  OwnedSide scale;
  List<OwnedSidesSettable> ownedSidesSettables = new ArrayList<>(2);

  public OwnedSidesSettableCommand(String name, Command onTrue, Command onFalse) {
    super(name, onTrue, onFalse);
    if (onTrue instanceof OwnedSidesSettable) ownedSidesSettables.add((OwnedSidesSettable) onTrue);
    if (onFalse instanceof OwnedSidesSettable)
      ownedSidesSettables.add((OwnedSidesSettable) onFalse);
  }

  @Override
  public final void setOwnedSide(
      StartPosition startPosition, OwnedSide nearSwitch, OwnedSide scale) {
    this.startPosition = startPosition;
    this.nearSwitch = nearSwitch;
    this.scale = scale;
    ownedSidesSettables.forEach(it -> it.setOwnedSide(startPosition, nearSwitch, scale));

    logger.debug(
        "{} set start position = {}, near switch = {}, scale = {}",
        getName(),
        startPosition,
        nearSwitch,
        scale);
  }

  boolean isSideOwned(OwnedSide side) {
    switch (startPosition) {
      case LEFT:
        return side == OwnedSide.LEFT;
      case RIGHT:
        return side == OwnedSide.RIGHT;
      default:
        logger.error(
            "{}: owned side {} is invalid for start position {}", getName(), side, startPosition);
        return false;
    }
  }
}
