package fi.metatavu.oioi.cm.liquibase.changes

import liquibase.database.Database
import liquibase.database.jvm.JdbcConnection
import liquibase.exception.CustomChangeException
import org.slf4j.LoggerFactory
import java.util.*

/**
 * Custom Liquibase migrations for adding all application new default content version
 */
class ContentVersionsChange: AbstractCustomTaskChange() {

    private val logger = LoggerFactory.getLogger(ContentVersionsChange::class.java)

    override fun getConfirmationMessage(): String {
        return "Added default content versions"
    }

    override fun execute(database: Database?) {
        database ?: throw CustomChangeException("No database connection")
        val connection: JdbcConnection = database.connection as JdbcConnection

        try {
            connection.prepareStatement(
                "SELECT " +
                    "  r.id as rootResourceId, r.lastModifierId as lastModifierId, a.id as applicationId, " +
                    "  a.device_id as deviceId, d.customer_id as customerId " +
                    "FROM " +
                    "  application a " +
                    "INNER JOIN resource r ON a.rootresource_id = r.id " +
                    "INNER JOIN device d ON d.id = a.device_id"
            ).use { statement ->
                statement.executeQuery().use { resultSet ->
                    while (resultSet.next()) {
                        val rootResourceId = getUUIDFromBytes(resultSet.getBytes(1))
                        val lastModifierId = getUUIDFromBytes(resultSet.getBytes(2))
                        val applicationId = getUUIDFromBytes(resultSet.getBytes(3))
                        val deviceId = getUUIDFromBytes(resultSet.getBytes(4))
                        val customerId = getUUIDFromBytes(resultSet.getBytes(5))

                        migrateContentVersion(
                            connection = connection,
                            rootResourceId = rootResourceId,
                            deviceId = deviceId,
                            applicationId = applicationId,
                            customerId = customerId,
                            lastModifierId = lastModifierId
                        )
                    }
                }

            }
        } catch (e: Exception) {
            throw CustomChangeException(e)
        }
    }

    /**
     * Migrate content version
     *
     * @param connection JDBC connection
     * @param rootResourceId root resource id
     * @param customerId customer id
     * @param deviceId device id
     * @param applicationId application id
     * @param lastModifierId last modifier id
     * @throws CustomChangeException when migration fails
     */
    private fun migrateContentVersion(connection: JdbcConnection, rootResourceId: UUID, deviceId: UUID, applicationId: UUID, customerId: UUID, lastModifierId: UUID) {
        val contentVersionId = UUID.randomUUID()

        createContentVersion(
            connection = connection,
            contentVersionId = contentVersionId,
            rootResourceId = rootResourceId,
            deviceId = deviceId,
            applicationId = applicationId,
            customerId = customerId,
            lastModifierId = lastModifierId
        )

        moveResourceToContentVersion(
            connection = connection,
            contentVersionId = contentVersionId,
            rootResourceId = rootResourceId
        )
    }

    /**
     * Create new content version resource
     *
     * @param connection JDBC connection
     * @param contentVersionId content version id
     * @param rootResourceId root resource id
     * @param customerId customer id
     * @param deviceId device id
     * @param applicationId application id
     * @param lastModifierId last modifier id
     * @throws CustomChangeException when migration fails
     */
    private fun createContentVersion(connection: JdbcConnection, contentVersionId: UUID, rootResourceId: UUID, customerId: UUID, deviceId: UUID, applicationId: UUID, lastModifierId: UUID) {
        logger.info("Creating content version $contentVersionId for root resource $rootResourceId")

        val keycloakResource = createProtectedResource(
            authzClient = authzClient,
            customerId = customerId,
            deviceId = deviceId,
            applicationId = applicationId,
            resourceId = contentVersionId,
            userId = lastModifierId
        ) ?: throw CustomChangeException("Failed to create Keycloak resource")

        val keycloakResourceId = getUUIDBytes(UUID.fromString(keycloakResource.id))

        try {
            connection.prepareStatement("INSERT INTO resource (id, parent_id, creatorid, lastmodifierid, keycloakresorceid, type, slug, name, createdat, modifiedat) values (?, ?, ?, ?, ?, 'CONTENT_VERSION', '1', '1', NOW(), NOW())").use { statement ->
                statement.setBytes(1, getUUIDBytes(contentVersionId))
                statement.setBytes(2, getUUIDBytes(rootResourceId))
                statement.setBytes(3, getUUIDBytes(lastModifierId))
                statement.setBytes(4, getUUIDBytes(lastModifierId))
                statement.setBytes(5, keycloakResourceId)
                statement.execute()
            }
        } catch (e: java.lang.Exception) {
            throw CustomChangeException(e)
        }
    }

    /**
     * Moves resources under root resource under content version
     *
     * @param connection JDBC connection
     * @param contentVersionId content version id
     * @param rootResourceId root resource id
     * @throws CustomChangeException when migration fails
     */
    private fun moveResourceToContentVersion(connection: JdbcConnection, contentVersionId: UUID, rootResourceId: UUID) {
        logger.info("Moving root resource $rootResourceId children to content version $contentVersionId")

        try {
            connection.prepareStatement("UPDATE resource SET parent_id = ? WHERE parent_id = ? AND id != ?").use { statement ->
                statement.setBytes(1, getUUIDBytes(contentVersionId))
                statement.setBytes(2, getUUIDBytes(rootResourceId))
                statement.setBytes(3, getUUIDBytes(contentVersionId))
                statement.execute()
            }
        } catch (e: java.lang.Exception) {
            throw CustomChangeException(e)
        }
    }

}