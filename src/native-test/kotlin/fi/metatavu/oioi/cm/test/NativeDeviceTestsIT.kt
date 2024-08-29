package fi.metatavu.oioi.cm.test

import fi.metatavu.oioi.cm.test.functional.resources.MysqlResource
import fi.metatavu.oioi.cm.test.functional.resources.HiveMQTestResource
import fi.metatavu.oioi.cm.test.functional.resources.KeycloakTestResource
import fi.metatavu.oioi.cm.test.functional.tests.DeviceTestsIT
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.NativeImageTest

/**
 * Native functional tests for devices
 *
 * @author Antti Leppä
 */
@NativeImageTest
@QuarkusTestResource.List(
    QuarkusTestResource(KeycloakTestResource::class),
    QuarkusTestResource(MysqlResource::class),
    QuarkusTestResource(HiveMQTestResource::class)
)
class NativeDeviceTestsIT : DeviceTestsIT() {

}