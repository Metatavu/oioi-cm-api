package fi.metatavu.oioi.cm.mqtt

import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.health.HealthCheck
import org.eclipse.microprofile.health.HealthCheckResponse
import java.net.InetSocketAddress
import java.net.Socket
import java.util.concurrent.TimeUnit
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Health check for MQTT connection
 *
 * @author Antti Leppä
 */
@ApplicationScoped
class MqttConnectionHealthCheck: HealthCheck {

    private val logger = org.slf4j.LoggerFactory.getLogger(MqttConnectionHealthCheck::class.java)

    @Inject
    @ConfigProperty(name = "mp.messaging.connector.smallrye-mqtt.host")
    lateinit var host: String

    @Inject
    @ConfigProperty(name = "mp.messaging.connector.smallrye-mqtt.port")
    lateinit var port: String

    override fun call(): HealthCheckResponse? {
        logger.info("Checking MQTT connection to $host:$port")

        return if (isMqttServerAlive(host, port.toInt())) {
            HealthCheckResponse.up("MQTT connection is healthy")
        } else {
            HealthCheckResponse.down("MQTT connection is not healthy")
        }
    }

    /**
     * Tests if MQTT server is alive
     *
     * @param host MQTT server host
     * @param port MQTT server port
     * @return true if MQTT server is alive; false otherwise
     */
    private fun isMqttServerAlive(
        host: String,
        port: Int
    ): Boolean {
        return try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress(host, port), TimeUnit.SECONDS.toMillis(5).toInt())
                true
            }
        } catch (e: Exception) {
            println("Failed to connect to MQTT server at $host:$port - ${e.message}")
            false
        }
    }

}