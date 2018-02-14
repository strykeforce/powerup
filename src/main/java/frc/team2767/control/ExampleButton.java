package frc.team2767.control;

import edu.wpi.first.wpilibj.buttons.Trigger;

public class ExampleButton extends Trigger {

  private final DriverControls controls;

  public ExampleButton(Controls controls) {
    this.controls = controls.getDriverControls();
  }

  @Override
  public boolean get() {
    return controls.getTuner() > 0.5;
  }
}
