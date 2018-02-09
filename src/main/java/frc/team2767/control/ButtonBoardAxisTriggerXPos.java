package frc.team2767.control;

import edu.wpi.first.wpilibj.buttons.Trigger;

public class ButtonBoardAxisTriggerXPos extends Trigger {

  private final Controls controls;

  public ButtonBoardAxisTriggerXPos(Controls controls) {
    this.controls = controls;
  }

  @Override
  public boolean get() {
    return controls.getButtonBoardXPos() > 0.9;
  }
}
