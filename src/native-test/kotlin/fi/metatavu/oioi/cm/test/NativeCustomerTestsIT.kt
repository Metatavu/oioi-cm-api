package fi.metatavu.oioi.cm.test

import fi.metatavu.oioi.cm.test.functional.tests.CustomerTestsIT
import io.quarkus.test.junit.NativeImageTest

/**
 * Native functional tests for customers
 *
 * @author Antti Leppä
 */
@NativeImageTest
class NativeCustomerTestsIT : CustomerTestsIT() {

}