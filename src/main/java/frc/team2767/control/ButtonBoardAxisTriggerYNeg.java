package frc.team2767.control;

import edu.wpi.first.wpilibj.buttons.Trigger;

public class ButtonBoardAxisTriggerYNeg extends Trigger {

  private final Controls controls;

  public ButtonBoardAxisTriggerYNeg(Controls controls) {
    this.controls = controls;
  }

  @Override
  public boolean get() {
    return Math.abs(controls.getButtonBoardYNeg()) > 0.9;
  }
}
