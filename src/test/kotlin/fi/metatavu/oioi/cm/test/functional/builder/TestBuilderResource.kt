package fi.metatavu.oioi.cm.test.functional.builder

import java.lang.Exception
import kotlin.Throws

/**
 * Interface describing a test builder resource.
 *
 * @author Antti Leppä
 *
 * @param <T> resource type
</T> */
interface TestBuilderResource<T> {
    /**
     * Adds closable into clean queue
     *
     * @param t closeable
     * @return given instance
     */
    fun addClosable(t: T): T

    /**
     * Cleans given resource
     *
     * @param t resource
     */
    @Throws(Exception::class)
    fun clean(t: T)
}