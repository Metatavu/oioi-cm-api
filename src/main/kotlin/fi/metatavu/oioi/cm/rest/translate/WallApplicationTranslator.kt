package fi.metatavu.oioi.cm.rest.translate

import fi.metatavu.oioi.cm.model.WallApplication
import fi.metatavu.oioi.cm.model.WallResource
import fi.metatavu.oioi.cm.persistence.model.Application
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

    override fun translate(entity: Application?): WallApplication? {
        entity ?: return null

        val root = wallResourceTranslator.translate(entity.rootResource)
        val result = WallApplication()
        result.modifiedAt = getModifiedAt(root)
        result.root = root
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
        modificationTimes.sortWith { a: OffsetDateTime?, b: OffsetDateTime -> b.compareTo(a) }
        return modificationTimes[0]
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