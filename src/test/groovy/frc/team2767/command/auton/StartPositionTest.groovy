package frc.team2767.command.auton

import spock.lang.Specification

class StartPositionTest extends Specification {

    def "Test angle conversion"() {
        expect:
        StartPosition.RIGHT.getPathAngle(90) == 0.0
        StartPosition.LEFT.getPathAngle(0.0) == 90.0
    }
}
