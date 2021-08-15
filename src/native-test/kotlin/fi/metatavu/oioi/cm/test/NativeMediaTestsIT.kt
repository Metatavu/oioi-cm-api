package fi.metatavu.oioi.cm.test

import fi.metatavu.oioi.cm.test.functional.tests.MediaTestsIT
import io.quarkus.test.junit.NativeImageTest

/**
 * Native functional tests for media
 *
 * @author Antti Leppä
 */
@NativeImageTest
class NativeMediaTestsIT : MediaTestsIT() {

}