package frc.team2767.control;

import edu.wpi.first.wpilibj.buttons.Trigger;

public class ExampleButton extends Trigger {

  private final Controls controls;

  public ExampleButton(Controls controls) {
    this.controls = controls;
  }

  @Override
  public boolean get() {
    return controls.getTuner() > 0.5;
  }
}
