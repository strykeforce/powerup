package frc.team2767.subsystem.health;

import java.util.List;

class TestCase {
  Test test;
  double output;
  Range current;
  Range speed;
  List<Result> results;

  boolean hasCurrentPassed(double current) {
    return this.current.inRange(current);
  }

  boolean hasSpeedPassed(int speed) {
    return this.speed.inRange(speed);
  }

  boolean hasPassed(double current, int speed) {
    return hasCurrentPassed(current) && hasSpeedPassed(speed);
  }

  String passFailString(double current, int speed) {
    return hasPassed(current, speed) ? "PASS" : "FAIL";
  }
}
