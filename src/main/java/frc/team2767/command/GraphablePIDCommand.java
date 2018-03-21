package frc.team2767.command;

import static org.strykeforce.thirdcoast.telemetry.grapher.Measure.*;

import com.squareup.moshi.JsonWriter;
import edu.wpi.first.wpilibj.command.PIDCommand;
import java.io.IOException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.DoubleSupplier;
import org.jetbrains.annotations.NotNull;
import org.strykeforce.thirdcoast.telemetry.grapher.Measure;
import org.strykeforce.thirdcoast.telemetry.item.Item;

public abstract class GraphablePIDCommand extends PIDCommand implements Item {

  private static final Set<Measure> MEASURES =
      Collections.unmodifiableSet(EnumSet.of(CLOSED_LOOP_TARGET, POSITION, CLOSED_LOOP_ERROR));

  public GraphablePIDCommand(String name, double p, double i, double d) {
    super(name, p, i, d);
  }

  @Override
  public int deviceId() {
    return 0;
  }

  @Override
  public String type() {
    return "pidCommand";
  }

  @Override
  public String description() {
    return getName();
  }

  @Override
  public Set<Measure> measures() {
    return MEASURES;
  }

  @Override
  public DoubleSupplier measurementFor(Measure measure) {
    if (!MEASURES.contains(measure)) {
      throw new IllegalArgumentException("invalid measure: " + measure.name());
    }

    switch (measure) {
      case CLOSED_LOOP_TARGET:
        return this::getSetpoint;
      case POSITION:
        return this::getPosition;
      case CLOSED_LOOP_ERROR:
        return () -> getPIDController().getError();
      default:
        throw new AssertionError(measure);
    }
  }

  @Override
  public void toJson(JsonWriter writer) throws IOException {}

  @Override
  public int compareTo(@NotNull Item other) {
    int result = type().compareTo(other.type());
    if (result != 0) return result;
    return Integer.compare(deviceId(), other.deviceId());
  }
}
