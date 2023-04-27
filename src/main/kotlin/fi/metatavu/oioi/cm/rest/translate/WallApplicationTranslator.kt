package fi.metatavu.oioi.cm.rest.translate

import fi.metatavu.oioi.cm.model.WallApplication
import fi.metatavu.oioi.cm.model.WallResource
import fi.metatavu.oioi.cm.persistence.model.Application
import fi.metatavu.oioi.cm.persistence.model.Resource
import java.time.OffsetDateTime
import java.util.*
import java.util.function.Consumer
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Translator for WallApplication
 *
 * @author Antti Leppä
 */
@ApplicationScoped
class WallApplicationTranslator : AbstractTranslator<Application?, WallApplication?>() {

    @Inject
    lateinit var wallResourceTranslator: WallResourceTranslator

    /**
     * Translator for WallApplication when specific content version is requested
     *
     * @param entity application
     * @param contentVersion content version
     */
    fun translate(entity: Application, contentVersion: Resource): WallApplication {
        val contentVersionWallResource = wallResourceTranslator.translate(entity = contentVersion, applicationName = entity.name)
        val result = WallApplication()
        result.modifiedAt = getModifiedAt(contentVersionWallResource)
        result.root = contentVersionWallResource
        return result
    }

    override fun translate(entity: Application?): WallApplication? {
        entity ?: return null

        val activeContentVersion = wallResourceTranslator.translate(entity = entity.activeContentVersionResource, applicationName = entity.name)
        val result = WallApplication()
        result.modifiedAt = getModifiedAt(activeContentVersion)
        result.root = activeContentVersion
        return result
    }

    /**
     * Returns most recent wall resource modification time
     *
     * @param root root resource
     * @return most recent wall resource modification time
     */
    private fun getModifiedAt(root: WallResource?): OffsetDateTime {
        val modificationTimes: MutableList<OffsetDateTime> = ArrayList()
        getModificationTimes(root, modificationTimes)
        return modificationTimes.maxOf { it }
    }

    /**
     * Recursively collects modification times from wall resources
     *
     * @param resource resource
     * @param result collected times
     */
    private fun getModificationTimes(resource: WallResource?, result: MutableList<OffsetDateTime>) {
        result.add(resource!!.modifiedAt)
        resource.children.forEach(Consumer { child: WallResource? -> getModificationTimes(child, result) })
    }

}