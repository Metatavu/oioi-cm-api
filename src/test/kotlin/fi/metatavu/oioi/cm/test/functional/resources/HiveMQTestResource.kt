package fi.metatavu.oioi.cm.test.functional.resources

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager
import org.testcontainers.hivemq.HiveMQContainer
import org.testcontainers.utility.DockerImageName

/**
 * Class for HiveMQ test resource.
 *
 * @author Antti Leppä
 */
class HiveMQTestResource: QuarkusTestResourceLifecycleManager {

    override fun start(): MutableMap<String, String> {
        hiveMQ.start()

        val config: MutableMap<String, String> = HashMap()
        config["mp.messaging.outgoing.resourcelocks.host"] = hiveMQ.host
        config["mp.messaging.outgoing.resourcelocks.port"] = hiveMQ.mqttPort.toString()

        return config
    }

    override fun stop() {
        hiveMQ.stop()
    }

    companion object {
        val hiveMQ: HiveMQContainer = HiveMQContainer(DockerImageName.parse("hivemq/hivemq-ce"))
    }
}