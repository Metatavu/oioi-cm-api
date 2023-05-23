package fi.metatavu.oioi.cm.liquibase.changes

import io.quarkus.runtime.annotations.RegisterForReflection
import liquibase.database.Database
import liquibase.database.jvm.JdbcConnection
import liquibase.exception.CustomChangeException
import org.slf4j.LoggerFactory
import java.util.*

/**
 * Custom Liquibase migration for copying resource order numbers from root resources to active content versions
 */
@RegisterForReflection
class ContentVersionOrderNumbersChange: AbstractCustomTaskChange() {

    companion object {
        private val logger = LoggerFactory.getLogger(ContentVersionOrderNumbersChange::class.java)
    }

    override fun getConfirmationMessage(): String {
        return "Copy order numbers from root resources to content versions"
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

                        copyResourceOrderNumber(
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
     * Copy resource order number from resource to another
     *
     * @param connection JDBC connection
     * @param fromResourceId move from resource id
     * @param toResourceId move to resource id
     * @throws CustomChangeException when migration fails
     */
    private fun copyResourceOrderNumber(connection: JdbcConnection, fromResourceId: UUID, toResourceId: UUID) {
        try {
            connection.prepareStatement("SELECT orderNumber FROM resource WHERE id = ?").use { fromStatement ->
                fromStatement.setBytes(1, getUUIDBytes(fromResourceId))
                fromStatement.executeQuery().use { resultSet ->
                    if (resultSet.next()) {
                        val orderNumber = resultSet.getInt(1)

                        connection.prepareStatement("UPDATE resource SET orderNumber = ? WHERE id = ?").use { statement ->
                            statement.setInt(1, orderNumber)
                            statement.setBytes(2, getUUIDBytes(toResourceId))
                            statement.execute()
                        }
                    }
                }
            }

            logger.info("Copy resource order number from {} to {}", fromResourceId, toResourceId)
        } catch (e: java.lang.Exception) {
            throw CustomChangeException(e)
        }
    }

}