package fi.metatavu.oioi.cm.test

import fi.metatavu.oioi.cm.test.functional.resources.MysqlResource
import fi.metatavu.oioi.cm.test.functional.resources.HiveMQTestResource
import fi.metatavu.oioi.cm.test.functional.resources.KeycloakTestResource
import fi.metatavu.oioi.cm.test.functional.tests.WallDeviceTestsIT
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.NativeImageTest

/**
 * Native functional tests for wall device export
 *
 * @author Antti Leppä
 */
@NativeImageTest
@QuarkusTestResource.List(
    QuarkusTestResource(KeycloakTestResource::class),
    QuarkusTestResource(MysqlResource::class),
    QuarkusTestResource(HiveMQTestResource::class)
)
class NativeWallDeviceTestsIT : WallDeviceTestsIT() {

}