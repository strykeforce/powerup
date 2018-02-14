package frc.team2767.control;

import javax.inject.Inject;

public class AlignWheelsTrigger extends Trigger {

  private final DriverControls controls;

  @Inject
  public AlignWheelsTrigger(DriverControls controls) {
    this.controls = controls;
  }

  @Override
  public boolean get() {
    return controls.getDriverLeftTrimXNeg() && controls.getDriverRightTrimXPos();
  }
}
