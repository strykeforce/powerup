package frc.team2767.subsystem.health;

class Range {
  double low, high;

  Range(double low, double high) {
    this.low = low;
    this.high = high;
  }

  boolean inRange(double val) {
    return val >= low && val <= high;
  }
}
