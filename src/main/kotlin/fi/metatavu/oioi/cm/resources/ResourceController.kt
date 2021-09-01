package fi.metatavu.oioi.cm.resources

import fi.metatavu.oioi.cm.authz.ResourceScope
import fi.metatavu.oioi.cm.copy.CopyException
import fi.metatavu.oioi.cm.model.KeyValueProperty
import fi.metatavu.oioi.cm.model.ResourceType
import fi.metatavu.oioi.cm.persistence.dao.ApplicationDAO
import fi.metatavu.oioi.cm.persistence.dao.ResourceDAO
import fi.metatavu.oioi.cm.persistence.dao.ResourcePropertyDAO
import fi.metatavu.oioi.cm.persistence.dao.ResourceStyleDAO
import fi.metatavu.oioi.cm.persistence.model.*
import org.keycloak.authorization.client.AuthzClient
import org.keycloak.representations.idm.authorization.ResourceRepresentation
import org.keycloak.representations.idm.authorization.ScopeRepresentation
import org.slf4j.Logger
import java.util.*
import java.util.function.Consumer
import java.util.stream.Collectors
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Controller for Resource
 *
 * @author Antti Leppä
 */
@ApplicationScoped
class ResourceController {

    @Inject
    lateinit var logger: Logger

    @Inject
    lateinit var resourceDAO: ResourceDAO

    @Inject
    lateinit var resourcePropertyDAO: ResourcePropertyDAO

    @Inject
    lateinit var resourceStyleDAO: ResourceStyleDAO

    @Inject
    lateinit var applicationDAO: ApplicationDAO

    /**
     * Create resource
     *
     * @param authzClient authzClient
     * @param customer customer
     * @param device device
     * @param application application
     * @param parent parent
     * @param data data
     * @param name name
     * @param slug slug
     * @param type type
     * @param properties properties
     * @param styles styles
     * @param creatorId creator id
     * @return created resource
     */
    fun createResource(
        authzClient: AuthzClient,
        customer: Customer,
        device: Device,
        application: Application,
        orderNumber: Int?,
        parent: Resource?,
        data: String?,
        name: String?,
        slug: String?,
        type: ResourceType?,
        properties: List<KeyValueProperty>,
        styles: List<KeyValueProperty>,
        creatorId: UUID
    ): Resource {
        val resourceId = UUID.randomUUID()
        val keycloakResourceId = createProtectedResource(authzClient, customer.id!!, device.id!!, application.id!!, resourceId, creatorId)

        val resource = resourceDAO.create(
            id = resourceId,
            orderNumber = orderNumber,
            data = data,
            keycloakResourceId = keycloakResourceId,
            name = name,
            parent = parent,
            slug = slug,
            type = type,
            creatorId = creatorId,
            lastModifierId = creatorId
        )

        setResourceProperties(resource, properties, creatorId)
        setResourceStyles(resource, styles, creatorId)

        return resource
    }

    /**
     * Create resource
     *
     * @param authzClient authzClient
     * @param customer customer
     * @param device device
     * @param applicationId application id
     * @param parent parent
     * @param data data
     * @param name name
     * @param slug slug
     * @param type type
     * @param creatorId creator id
     * @return created resource
     */
    fun createResource(
        authzClient: AuthzClient,
        customer: Customer,
        device: Device,
        applicationId: UUID,
        orderNumber: Int?,
        parent: Resource?,
        data: String?,
        name: String?,
        slug: String?,
        type: ResourceType?,
        creatorId: UUID
    ): Resource {
        val id = UUID.randomUUID()
        val keycloakResourceId = createProtectedResource(
            authzClient = authzClient,
            resourceId = id,
            customerId = customer.id!!,
            deviceId = device.id!!,
            applicationId = applicationId,
            userId = creatorId
        )

        return resourceDAO.create(
            id = id,
            orderNumber = orderNumber,
            data = data,
            keycloakResourceId = keycloakResourceId,
            name = name,
            parent = parent,
            slug = slug,
            type = type,
            creatorId = creatorId,
            lastModifierId = creatorId
        )
    }



    /**
     * Copies resource and child resources recursively
     *
     * @param authzClient authzClient
     * @param source source
     * @param targetApplication target application
     * @param targetParent new parent
     * @param creatorId creator id
     * @return copied resource
     **/
    fun copyResource(
        authzClient: AuthzClient,
        targetApplication: Application,
        source: Resource,
        targetParent: Resource,
        creatorId: UUID
    ): Resource? {
        if (source.parent?.type != targetParent.type) {
            throw CopyException("Cannot copy from source parent with ${source.parent?.type} to target parent with type ${targetParent.type}")
        }

        val id = UUID.randomUUID()
        val targetDevice = targetApplication.device
        val targetCustomer = targetDevice?.customer

        val keycloakResourceId = createProtectedResource(
            authzClient = authzClient,
            resourceId = id,
            customerId = targetCustomer?.id ?: throw CopyException("Could not resolve target customer"),
            deviceId = targetDevice.id ?: throw CopyException("Could not resolve target device"),
            applicationId = targetApplication.id ?: throw CopyException("Could not resolve target application"),
            userId = creatorId
        )

        val result = resourceDAO.create(
            id = id,
            orderNumber = source.orderNumber,
            data = source.data,
            keycloakResourceId = keycloakResourceId,
            name = source.name,
            parent = targetParent,
            slug = source.slug,
            type = source.type,
            creatorId = creatorId,
            lastModifierId = creatorId
        )

        copyChildResources(
            authzClient = authzClient,
            targetApplication = targetApplication,
            source = source,
            targetParent = result,
            creatorId = creatorId
        )

        return result
    }

    /**
     * Find resource by id
     *
     * @param id resource id
     * @return found resource or null if not found
     */
    fun findResourceById(id: UUID?): Resource? {
        return resourceDAO.findById(id)
    }

    /**
     * Lists resources by parent
     *
     * @param parent parent
     * @return resources
     */
    fun listResourcesByParent(parent: Resource?): List<Resource> {
        return resourceDAO.listByParent(parent)
    }

    /**
     * Update resource
     *
     * @param resource resource
     * @param orderNumber orderNumber
     * @param data data
     * @param name name
     * @param parent parent
     * @param slug slug
     * @param type type
     * @param lastModifierId last modifier id
     * @return updated resource
     */
    fun updateResource(
        resource: Resource,
        orderNumber: Int?,
        data: String?,
        name: String?,
        parent: Resource?,
        slug: String?,
        type: ResourceType?,
        lastModifierId: UUID?
    ): Resource {
        resourceDAO.updateData(resource, data, lastModifierId)
        resourceDAO.updateName(resource, name, lastModifierId)
        resourceDAO.updateParent(resource, parent, lastModifierId)
        resourceDAO.updateSlug(resource, slug, lastModifierId)
        resourceDAO.updateType(resource, type, lastModifierId)
        resourceDAO.updateOrderNumber(resource, orderNumber, lastModifierId)
        return resource
    }

    /**
     * Lists resource styles
     *
     * @param resource resource
     * @return resource styles
     */
    fun listStyles(resource: Resource?): List<ResourceStyle> {
        return resourceStyleDAO.listByResource(resource)
    }

    /**
     * Lists resource properties
     *
     * @param resource resource
     * @return resource properties
     */
    fun listProperties(resource: Resource?): List<ResourceProperty> {
        return resourcePropertyDAO.listByResource(resource)
    }

    /**
     * Deletes an resource
     *
     * @param authzClient authzClient
     * @param resource resource to be deleted
     */
    fun delete(authzClient: AuthzClient, resource: Resource) {
        listResourcesByParent(resource).forEach(Consumer { child: Resource -> delete(authzClient, child) })
        listProperties(resource).forEach(Consumer { resourceProperty: ResourceProperty ->
            deleteProperty(
                resourceProperty
            )
        })
        listStyles(resource).forEach(Consumer { resourceStyle: ResourceStyle -> deleteStyle(resourceStyle) })
        val keycloakResourceId = resource.keycloakResorceId
        try {
            authzClient.protection().resource().delete(keycloakResourceId.toString())
        } catch (e: Exception) {
            if (logger.isErrorEnabled) {
                logger.error(String.format("Failed to remove Keycloak resource %s ", resource.keycloakResorceId), e)
            }
        }
        resourceDAO.delete(resource)
    }

    /**
     * Sets resource styles
     *
     * @param resource resource
     * @param styles styles
     * @param lastModifierId modifier
     */
    fun setResourceStyles(resource: Resource?, styles: List<KeyValueProperty>, lastModifierId: UUID) {
        val existingStyles = listStyles(resource).associateBy { it.key }.toMutableMap()

        for (meta in styles) {
            val existingStyle = existingStyles.remove(meta.key)
            if (existingStyle == null) {
                createResourceStyle(resource, meta.key, meta.value, lastModifierId)
            } else {
                updateResourceStyle(existingStyle, meta.key, meta.value, lastModifierId)
            }
        }

        existingStyles.values.forEach(this::deleteStyle)
    }

    /**
     * Sets resource properties
     *
     * @param resource resource
     * @param properties properties
     * @param lastModifierId modifier
     */
    fun setResourceProperties(resource: Resource?, properties: List<KeyValueProperty>, lastModifierId: UUID) {
        val existingProperties = listProperties(resource).associateBy { it.key }.toMutableMap()

        for (meta in properties) {
            val existingProperty = existingProperties.remove(meta.key)
            if (existingProperty == null) {
                createResourceProperty(resource, meta.key, meta.value, lastModifierId)
            } else {
                updateResourceProperty(existingProperty, meta.key, meta.value, lastModifierId)
            }
        }

        existingProperties.values.forEach(this::deleteProperty)
    }

    /**
     * Returns whether this resource belongs to given application
     *
     * @param application application
     * @param resource resource
     * @return whether this resource belongs to given application
     */
    fun isApplicationResource(application: Application, resource: Resource): Boolean {
        val resourceApplication = getResourceApplication(resource = resource) ?: return false
        return application.id == resourceApplication.id
    }

    /**
     * Resolves application for given resource
     *
     * @param resource resource
     * @return application or null if not found
     */
    fun getResourceApplication(resource: Resource): Application? {
        val rootResource = getResourceRootResource(resource = resource) ?: return null
        return applicationDAO.findByRootResource(rootResource = rootResource)
    }

    /**
     * Create resource property
     *
     * @param resource resource
     * @param key key
     * @param value value
     * @param creatorId creator id
     * @return created resourceProperty
     */
    private fun createResourceProperty(
        resource: Resource?,
        key: String,
        value: String,
        creatorId: UUID
    ): ResourceProperty {
        return resourcePropertyDAO.create(UUID.randomUUID(), key, value, resource, creatorId, creatorId)
    }

    /**
     * Update resource property
     *
     * @param resourceProperty property
     * @param key key
     * @param value value
     * @param lastModifierId last modifier id
     * @return updated resourceProperty
     */
    private fun updateResourceProperty(
        resourceProperty: ResourceProperty,
        key: String,
        value: String,
        lastModifierId: UUID
    ): ResourceProperty {
        resourcePropertyDAO.updateKey(resourceProperty, key, lastModifierId)
        resourcePropertyDAO.updateValue(resourceProperty, value, lastModifierId)
        return resourceProperty
    }

    /**
     * Create resource style
     *
     * @param resource resource
     * @param key key
     * @param value value
     * @param creatorId creator id
     * @return created resourceStyle
     */
    private fun createResourceStyle(resource: Resource?, key: String, value: String, creatorId: UUID): ResourceStyle {
        return resourceStyleDAO.create(UUID.randomUUID(), key, value, resource, creatorId, creatorId)
    }

    /**
     * Update resource style
     *
     * @param resourceStyle resource style
     * @param key key
     * @param value value
     * @param lastModifierId last modifier id
     * @return updated resourceStyle
     */
    private fun updateResourceStyle(
        resourceStyle: ResourceStyle,
        key: String,
        value: String,
        lastModifierId: UUID
    ): ResourceStyle {
        resourceStyleDAO.updateKey(resourceStyle, key, lastModifierId)
        resourceStyleDAO.updateValue(resourceStyle, value, lastModifierId)
        return resourceStyle
    }

    /**
     * Deletes a resource style
     *
     * @param resourceStyle resource style
     */
    private fun deleteStyle(resourceStyle: ResourceStyle) {
        resourceStyleDAO.delete(resourceStyle)
    }

    /**
     * Deletes a resource property
     *
     * @param resourceProperty property style
     */
    private fun deleteProperty(resourceProperty: ResourceProperty) {
        resourcePropertyDAO.delete(resourceProperty)
    }

    /**
     * Copies resource child resources recursively
     *
     * @param authzClient authzClient
     * @param source source
     * @param targetApplication target application
     * @param targetParent new parent
     * @param creatorId creator id
     * @return copied resource
     **/
    private fun copyChildResources(
        authzClient: AuthzClient,
        source: Resource,
        targetApplication: Application,
        targetParent: Resource,
        creatorId: UUID
    ) {
        val sourceChildResources = resourceDAO.listByParent(parent = source)
        sourceChildResources.forEach { sourceChildResource ->
            copyResource(
                authzClient = authzClient,
                targetApplication = targetApplication,
                source = sourceChildResource,
                targetParent = targetParent,
                creatorId = creatorId
            )
        }
    }

    /**
     * Resolves root resource for given resource
     *
     * @param resource resource
     * @return root resource or null if not found
     */
    private fun getResourceRootResource(resource: Resource): Resource? {
        var rootResource = resource

        while (rootResource.type != ResourceType.ROOT) {
            rootResource = rootResource.parent ?: return null
        }

        return rootResource
    }

    /**
     * Creates protected resource to Keycloak
     *
     * @param authzClient authz client
     * @param customerId customer id
     * @param deviceId device id
     * @param applicationId application id
     * @param resourceId resource id
     * @param userId userId
     *
     * @return created resource id
     */
    private fun createProtectedResource(
        authzClient: AuthzClient,
        customerId: UUID,
        deviceId: UUID,
        applicationId: UUID,
        resourceId: UUID,
        userId: UUID
    ): UUID? {
        val scopes = Arrays.stream(ResourceScope.values())
            .map { obj: ResourceScope -> obj.scope }
            .map { name: String? -> ScopeRepresentation(name) }
            .collect(Collectors.toSet())

        val resourceUri = String.format(
            "/v1/%s/devices/%s/applications/%s/resources/%s",
            customerId,
            deviceId,
            applicationId,
            resourceId
        )

        val keycloakResource = ResourceRepresentation(
            resourceId.toString(),
            scopes,
            resourceUri,
            fi.metatavu.oioi.cm.authz.ResourceType.RESOURCE.type
        )

        keycloakResource.setOwner(userId.toString())
        keycloakResource.ownerManagedAccess = true
        val result = authzClient.protection().resource().create(keycloakResource)
        if (result != null) {
            return UUID.fromString(result.id)
        }

        val resources = authzClient.protection().resource().findByUri(resourceUri)
        return if (resources.isNotEmpty()) {
            UUID.fromString(resources[0].id)
        } else null
    }
}