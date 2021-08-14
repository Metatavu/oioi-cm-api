package fi.metatavu.oioi.cm.test

import fi.metatavu.oioi.cm.test.functional.tests.DeviceTestsIT
import io.quarkus.test.junit.NativeImageTest

/**
 * Native functional tests for devices
 *
 * @author Antti Leppä
 */
@NativeImageTest
class NativeDeviceTestsIT : DeviceTestsIT() {

}