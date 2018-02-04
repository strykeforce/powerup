package frc.team2767.trigger;

import frc.team2767.Controls;
import javax.inject.Inject;

public class AlignWheelsTrigger extends Trigger {

  private final Controls controls;

  @Inject
  public AlignWheelsTrigger(Controls controls) {
    this.controls = controls;
  }

  @Override
  public boolean get() {
    return controls.getGamepadBackButton() && controls.getGamepadStartButton();
  }
}
