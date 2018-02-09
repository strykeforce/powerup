package frc.team2767.control;

import edu.wpi.first.wpilibj.buttons.Trigger;

public class ButtonBoardAxisTriggerXNeg extends Trigger {

  private final Controls controls;

  public ButtonBoardAxisTriggerXNeg(Controls controls) {
    this.controls = controls;
  }

  @Override
  public boolean get() {
    return controls.getButtonBoardXNeg() < -0.9;
  }
}
