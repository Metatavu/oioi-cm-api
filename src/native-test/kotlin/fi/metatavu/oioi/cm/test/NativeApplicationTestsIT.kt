package fi.metatavu.oioi.cm.test

import fi.metatavu.oioi.cm.test.functional.tests.ApplicationTestsIT
import io.quarkus.test.junit.NativeImageTest

/**
 * Native functional tests for applications
 *
 * @author Antti Leppä
 */
@NativeImageTest
class NativeApplicationTestsIT : ApplicationTestsIT() {

}