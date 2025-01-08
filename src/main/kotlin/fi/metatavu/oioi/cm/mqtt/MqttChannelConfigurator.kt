package fi.metatavu.oioi.cm.mqtt

import io.vertx.core.Vertx
import io.vertx.mqtt.MqttClient
import io.vertx.mqtt.MqttClientOptions
import org.eclipse.microprofile.config.ConfigProvider
import org.eclipse.microprofile.config.spi.ConfigSource
import java.net.URI
import java.util.concurrent.atomic.AtomicReference

/**
 * Configurator for MQTT channel
 */
class MqttChannelConfigurator: ConfigSource {

    private val logger = org.slf4j.LoggerFactory.getLogger(MqttChannelConfigurator::class.java)
    private var activeUrl: URI? = null

    override fun getPropertyNames(): MutableSet<String> {
        return emptySet<String>().toMutableSet()
    }

    override fun getValue(propertyName: String?): String? {
        return when (propertyName) {
            "mp.messaging.connector.smallrye-mqtt.host" -> getActiveUrl().host
            "mp.messaging.connector.smallrye-mqtt.port" -> getActiveUrl().port.toString()
            else -> null
        }
    }

    override fun getName(): String {
        return MqttChannelConfigurator::class.java.name
    }

    override fun getOrdinal(): Int {
        return 500
    }

    /**
     * Returns active MQTT server URL
     *
     * @return active MQTT server URL
     */
    private fun getActiveUrl(): URI {
        if (activeUrl != null) {
            return activeUrl!!
        }

        val urls = parseUrls()
        activeUrl = urls.first { isMqttServerAlive(it) }

        return activeUrl!!
    }

    /**
     * Tests if MQTT server is alive
     *
     * @param url MQTT server URL
     * @return true if MQTT server is alive; false otherwise
     */
    private fun isMqttServerAlive(url: URI): Boolean {
        logger.info("Testing MQTT server: $url")

        val vertx = Vertx.vertx()
        val options = MqttClientOptions().apply {
            isSsl = url.scheme.contains("ssl")
        }

        val client = MqttClient.create(vertx, options)
        val connected = AtomicReference(false)
        val latch = java.util.concurrent.CountDownLatch(1)

        val port = url.port
        val host = url.host

        client?.connect(port, host) { result ->
            try {
                val success = result.succeeded()
                connected.set(success)

                if (success) {
                    logger.info("Connection succeeded to MQTT server: $url")
                    client.disconnect()
                } else {
                    logger.info("Failed to connect to MQTT server: $url")
                }
            } catch (e: Exception) {
                connected.set(false)
            } finally {
                latch.countDown()
            }
        }

        latch.await()

        return connected.get()
    }

    /**
     * Parses MQTT server URLs from configuration
     *
     * @return MQTT server URLs
     */
    private fun parseUrls(): List<URI> {
        val urls = ConfigProvider.getConfig().getValue("mqtt.urls", String::class.java)
        return urls.split(",").map { URI.create(it) }
    }
}