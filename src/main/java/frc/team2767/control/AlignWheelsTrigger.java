package frc.team2767.control;

import static frc.team2767.control.DriverControls.Trim.LEFT_X_NEG;
import static frc.team2767.control.DriverControls.Trim.LEFT_X_POS;

import javax.inject.Inject;

public class AlignWheelsTrigger extends SimpleTrigger {

  private final DriverControls controls;

  @Inject
  public AlignWheelsTrigger(DriverControls controls) {
    this.controls = controls;
  }

  @Override
  public boolean get() {
    return controls.getTrim(LEFT_X_NEG) && controls.getTrim(LEFT_X_POS);
  }
}
