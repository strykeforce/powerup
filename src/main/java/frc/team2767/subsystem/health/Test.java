package frc.team2767.subsystem.health;

import java.util.List;
import org.slf4j.Logger;

class Test {
  String name;
  TestType type;
  List<Integer> ids;
  List<TestCase> testCases;

  void log(Logger logger) {
    logger.info(name);
    logger.info(String.format("%2s  %4s   %4s   %6s", "id", "volt", "curr", "speed"));

    for (TestCase tc : testCases) {
      for (Result result : tc.results) {
        logger.info(
            String.format(
                "%2d  %4.1f   %4.2f   %6d  %s",
                result.id,
                tc.output * 12,
                result.current,
                result.velocity,
                tc.passFailString(result.current, result.velocity)));
      }
    }
  }
}
