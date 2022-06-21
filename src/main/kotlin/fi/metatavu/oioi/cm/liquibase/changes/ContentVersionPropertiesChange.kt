package fi.metatavu.oioi.cm.liquibase.changes

import io.quarkus.runtime.annotations.RegisterForReflection
import liquibase.database.Database
import liquibase.database.jvm.JdbcConnection
import liquibase.exception.CustomChangeException
import org.slf4j.LoggerFactory
import java.util.*

/**
 * Custom Liquibase migration for moving resource properties and styles from root resources to active content versions
 */
@RegisterForReflection
class ContentVersionPropertiesChange: AbstractCustomTaskChange() {

    companion object {
        private val logger = LoggerFactory.getLogger(ContentVersionPropertiesChange::class.java)
    }

    override fun getConfirmationMessage(): String {
        return "Move properties from root resources to content versions"
    }

    override fun execute(database: Database?) {
        database ?: throw CustomChangeException("No database connection")
        val connection: JdbcConnection = database.connection as JdbcConnection

        try {
            connection.prepareStatement("SELECT rootresource_id, activecontentversionresource_id FROM application").use { statement ->
                statement.executeQuery().use { resultSet ->
                    while (resultSet.next()) {
                        val rootResourceId = getUUIDFromBytes(resultSet.getBytes(1))
                        val activeContentVersionResourceId = getUUIDFromBytes(resultSet.getBytes(2))

                        moveResourceProperties(
                            connection = connection,
                            fromResourceId = rootResourceId,
                            toResourceId = activeContentVersionResourceId
                        )

                        moveResourceStyles(
                            connection = connection,
                            fromResourceId = rootResourceId,
                            toResourceId = activeContentVersionResourceId
                        )
                    }
                }

            }
        } catch (e: Exception) {
            throw CustomChangeException(e)
        }
    }

    /**
     * Move resource properties from resource to another
     *
     * @param connection JDBC connection
     * @param fromResourceId move from resource id
     * @param toResourceId move to resource id
     * @throws CustomChangeException when migration fails
     */
    private fun moveResourceProperties(connection: JdbcConnection, fromResourceId: UUID, toResourceId: UUID) {
        try {
            // Remove duplicates from the source resource

            connection.prepareStatement("SELECT propertykey FROM resourceproperty WHERE resource_id = ?").use { keyStatement ->
                keyStatement.setBytes(1, getUUIDBytes(toResourceId))
                keyStatement.executeQuery().use { resultSet ->
                    while (resultSet.next()) {
                        val key = resultSet.getString(1)

                        connection.prepareStatement("DELETE FROM resourceproperty WHERE resource_id = ? AND propertykey = ?").use { statement ->
                            statement.setBytes(1, getUUIDBytes(fromResourceId))
                            statement.setString(2, key)
                            statement.execute()
                        }
                    }
                }
            }

            // Move everything else to target resource

            connection.prepareStatement("UPDATE resourceproperty SET resource_id = ? WHERE resource_id = ?").use { statement ->
                statement.setBytes(1, getUUIDBytes(toResourceId))
                statement.setBytes(2, getUUIDBytes(fromResourceId))
                statement.execute()
            }

            logger.info("Moved resource properties from {} to {}", fromResourceId, toResourceId)
        } catch (e: java.lang.Exception) {
            throw CustomChangeException(e)
        }
    }

    /**
     * Move resource styles from resource to another
     *
     * @param connection JDBC connection
     * @param fromResourceId move from resource id
     * @param toResourceId move to resource id
     * @throws CustomChangeException when migration fails
     */
    private fun moveResourceStyles(connection: JdbcConnection, fromResourceId: UUID, toResourceId: UUID) {
        try {
            // Remove duplicates from the source resource

            connection.prepareStatement("SELECT stylekey FROM resourcestyle WHERE resource_id = ?").use { keyStatement ->
                keyStatement.setBytes(1, getUUIDBytes(toResourceId))
                keyStatement.executeQuery().use { resultSet ->
                    while (resultSet.next()) {
                        val key = resultSet.getString(1)

                        connection.prepareStatement("DELETE FROM resourcestyle WHERE resource_id = ? AND stylekey = ?").use { statement ->
                            statement.setBytes(1, getUUIDBytes(fromResourceId))
                            statement.setString(2, key)
                            statement.execute()
                        }
                    }
                }
            }

            connection.prepareStatement("UPDATE resourcestyle SET resource_id = ? WHERE resource_id = ?").use { statement ->
                statement.setBytes(1, getUUIDBytes(toResourceId))
                statement.setBytes(2, getUUIDBytes(fromResourceId))
                statement.execute()
            }

            logger.info("Moved resource styles from {} to {}", fromResourceId, toResourceId)
        } catch (e: java.lang.Exception) {
            throw CustomChangeException(e)
        }
    }

}