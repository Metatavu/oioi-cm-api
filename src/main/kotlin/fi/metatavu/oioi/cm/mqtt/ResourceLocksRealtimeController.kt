package fi.metatavu.oioi.cm.mqtt

import fi.metatavu.oioi.cm.model.MqttResourceLockUpdate
import org.eclipse.microprofile.reactive.messaging.Channel
import org.eclipse.microprofile.reactive.messaging.Emitter
import org.slf4j.Logger
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * controller for resource lock realtime operations
 */
@ApplicationScoped
@Suppress ("UNUSED")
class ResourceLocksRealtimeController {

    @Inject
    lateinit var logger: Logger

    @Inject
    @Channel ("resourcelocks")
    lateinit var channel: Emitter<MqttResourceLockUpdate>

    /**
     * Notifies resource lock change via MQTT realtime channel
     *
     * @param resourceId resource id
     * @param locked whether resource was locked or unlocked
     */
    fun notifyResourceLockChange(resourceId: UUID, locked: Boolean) {
        val payload = MqttResourceLockUpdate()
        payload.resourceId = resourceId
        payload.locked = locked
        channel.send(payload)
    }

}