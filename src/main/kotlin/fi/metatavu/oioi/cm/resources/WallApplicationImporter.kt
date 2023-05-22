package fi.metatavu.oioi.cm.resources

import fi.metatavu.oioi.cm.model.KeyValueProperty
import fi.metatavu.oioi.cm.model.WallApplication
import fi.metatavu.oioi.cm.model.WallResource
import fi.metatavu.oioi.cm.persistence.model.Application
import fi.metatavu.oioi.cm.persistence.model.Customer
import fi.metatavu.oioi.cm.persistence.model.Device
import org.keycloak.authorization.client.AuthzClient
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Importer for wall application JSON
 */
@ApplicationScoped
class WallApplicationImporter {

    @Inject
    lateinit var resourceController: ResourceController

    /**
     * Imports new content version from wall application JSON.
     *
     * This method assumes that the root resource is content version,
     * so caller must ensure that the root resource is actually a content version.
     *
     * @param authzClient authz client
     * @param wallApplication wall application JSON
     * @param customer customer
     * @param device device
     * @param application application
     * @param loggedUserId logged user id
     *
     * @return imported content version
     */
    fun importFromWallApplicationJSON(
        authzClient: AuthzClient,
        wallApplication: WallApplication,
        customer: Customer,
        device: Device,
        application: Application,
        loggedUserId: UUID
    ): fi.metatavu.oioi.cm.persistence.model.Resource {
        return importWallResource(
            authzClient = authzClient,
            wallResource = wallApplication.root,
            parent = application.rootResource,
            orderNumber = 0,
            customer = customer,
            device = device,
            application = application,
            loggedUserId = loggedUserId
        )
    }

    /**
     * Imports a wall resource and its children
     *
     * @param authzClient authz client
     * @param wallResource wall resource
     * @param parent parent resource
     * @param orderNumber order number
     * @param customer customer
     * @param device device
     * @param application application
     * @param loggedUserId logged user id
     *
     * @return imported resource
     */
    private fun importWallResource(
        authzClient: AuthzClient,
        wallResource: WallResource,
        parent: fi.metatavu.oioi.cm.persistence.model.Resource?,
        orderNumber: Int,
        customer: Customer,
        device: Device,
        application: Application,
        loggedUserId: UUID
    ): fi.metatavu.oioi.cm.persistence.model.Resource {
        val resource = resourceController.createResource(
            authzClient = authzClient,
            customer = customer,
            device = device,
            application = application,
            orderNumber = orderNumber,
            parent = parent,
            data = wallResource.data,
            name = wallResource.name,
            slug = wallResource.slug,
            type = wallResource.type,
            properties = wallResource.properties.entries.map(this::toKeyValueProperty),
            styles = wallResource.styles.entries.map(this::toKeyValueProperty),
            creatorId = loggedUserId
        )

        wallResource.children.forEachIndexed { index, child ->
            importWallResource(
                authzClient = authzClient,
                wallResource = child,
                parent = resource,
                orderNumber = index,
                customer = customer,
                device = device,
                application = application,
                loggedUserId = loggedUserId
            )
        }

        return resource
    }

    /**
     * Converts map entry to key value property
     *
     * @param entry map entry
     * @return converted key value property
     */
    private fun toKeyValueProperty(entry: MutableMap.MutableEntry<String, String>): KeyValueProperty  {
        val result = KeyValueProperty()
        result.key = entry.key
        result.value = entry.value
        return result
    }

}