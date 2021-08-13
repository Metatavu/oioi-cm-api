package fi.metatavu.oioi.cm.test.functional.tests

import fi.metatavu.oioi.cm.client.models.KeyValueProperty
import org.junit.After
import fi.metatavu.oioi.cm.test.functional.builder.TestBuilder
import org.junit.Assert.*

/**
 * Abstract base class for functional tests
 *
 * @author Antti Leppä
 */
abstract class AbstractFunctionalTest {

    @After
    fun assetCleanAfter() {
        TestBuilder().use { builder -> assertEquals(0, builder.admin().customers.listCustomers().size) }
    }

    /**
     * Creates key value object
     *
     * @param key key
     * @param value value
     * @return key value object
     */
    protected fun getKeyValue(key: String?, value: String?): KeyValueProperty {
        return KeyValueProperty(key!!, value!!)
    }

}