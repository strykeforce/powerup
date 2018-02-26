package frc.team2767.subsystem

import com.ctre.phoenix.motorcontrol.can.TalonSRX
import org.strykeforce.thirdcoast.swerve.SwerveDrive
import org.strykeforce.thirdcoast.util.Settings
import spock.lang.Specification

import static com.ctre.phoenix.motorcontrol.ControlMode.Velocity

class PowerUpWheelTest extends Specification {

    def azimuth = Mock(TalonSRX)
    def drive = Mock(TalonSRX)

    def "drive shifts profile slots"() {
        given:
        def tomlStr = "[THIRDCOAST.WHEEL]\ndriveSetpointMax=10_000"
        def wheel = new PowerUpWheel(new Settings(tomlStr), azimuth, drive)
        wheel.setDriveMode(SwerveDrive.DriveMode.TRAJECTORY)

        when:
        wheel.set(0, 0)
        then:
        1 * drive.selectProfileSlot(0, 0)
        1 * drive.set(Velocity, 0)
        0 * drive._

        when:
        wheel.set(0, 0.01)
        then:
        1 * drive.set(Velocity, 100)
        0 * drive._

        when:
        wheel.set(0, 0.3)
        then:
        1 * drive.set(Velocity, 3000)
        0 * drive._

        when:
        wheel.set(0, 0.3)
        then:
        1 * drive.set(Velocity, 3000)
        0 * drive._

        when:
        wheel.set(0, 0.301)
        then:
        1 * drive.selectProfileSlot(1, 0)
        1 * drive.set(Velocity, 3010)
        0 * drive._

        when:
        wheel.set(0, 0.3)
        then:
        1 * drive.set(Velocity, 3000)
        0 * drive._

        when:
        wheel.set(0, 0.6)
        then:
        1 * drive.set(Velocity, 6000)
        0 * drive._

        when:
        wheel.set(0, 0.601)
        then:
        1 * drive.selectProfileSlot(2, 0)
        1 * drive.set(Velocity, 6010)
        0 * drive._

        when:
        wheel.set(0, 0.5)
        then:
        1 * drive.set(Velocity, 5000)
        0 * drive._

        when:
        wheel.set(0, 0.499)
        then:
        1 * drive.selectProfileSlot(1, 0)
        1 * drive.set(Velocity, 4990)
        0 * drive._

        when:
        wheel.set(0, 0.25)
        then:
        1 * drive.set(Velocity, 2500)
        0 * drive._

        when:
        wheel.set(0, 0.249)
        then:
        1 * drive.selectProfileSlot(0, 0)
        1 * drive.set(Velocity, 2490)
        0 * drive._
    }

    def "drive only selects slot once"() {
        given:
        def tomlStr = "[THIRDCOAST.WHEEL]\ndriveSetpointMax=10_000"
        def wheel = new PowerUpWheel(new Settings(tomlStr), azimuth, drive)
        wheel.setDriveMode(SwerveDrive.DriveMode.TRAJECTORY)

        when:
        wheel.set(0, 0.01)
        wheel.set(0, 0.02)

        then:
        1 * drive.selectProfileSlot(0, 0)
    }
}
