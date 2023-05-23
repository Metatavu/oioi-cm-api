package fi.metatavu.oioi.cm.rest

import fi.metatavu.oioi.cm.customers.CustomerController
import fi.metatavu.oioi.cm.medias.MediaController
import fi.metatavu.oioi.cm.model.*
import fi.metatavu.oioi.cm.resources.ResourceController
import fi.metatavu.oioi.cm.rest.translate.*
import fi.metatavu.oioi.cm.spec.MediasApi
import java.util.*
import javax.enterprise.context.RequestScoped
import javax.inject.Inject
import javax.transaction.Transactional
import javax.ws.rs.Consumes
import javax.ws.rs.Produces
import javax.ws.rs.core.Response

/**
 * REST - endpoints for medias
 *
 * @author Antti Leppä
 */
@RequestScoped
@Transactional
@Consumes("application/json;charset=utf-8")
@Produces("application/json;charset=utf-8")
class MediasApiImpl : AbstractApi(), MediasApi {

    @Inject
    lateinit var customerController: CustomerController

    @Inject
    lateinit var resourceController: ResourceController

    @Inject
    lateinit var mediaController: MediaController

    @Inject
    lateinit var mediaTranslator: MediaTranslator

    override fun createMedia(customerId: UUID, media: Media): Response {
        val loggedUserId = loggedUserId!!
        val customer = customerController.findCustomerById(customerId)
            ?: return createNotFound(NOT_FOUND_MESSAGE)
        return if (!isAdminOrHasCustomerGroup(customer.name)) {
            createForbidden(FORBIDDEN_MESSAGE)
        } else createOk(
            mediaTranslator.translate(
                mediaController.createMedia(
                    customer,
                    media.contentType,
                    media.type,
                    media.url,
                    loggedUserId
                )
            )
        )
    }

    override fun listMedias(customerId: UUID, type: MediaType?): Response {
        val customer = customerController.findCustomerById(customerId) ?: return createNotFound(NOT_FOUND_MESSAGE)
        return if (!isAdminOrHasCustomerGroup(customer.name)) {
            createForbidden(FORBIDDEN_MESSAGE)
        } else createOk(mediaTranslator.translate(mediaController.listMedias(customer, type)))
    }

    override fun findMedia(customerId: UUID, mediaId: UUID): Response {
        val customer = customerController.findCustomerById(customerId)
            ?: return createNotFound(NOT_FOUND_MESSAGE)
        if (!isAdminOrHasCustomerGroup(customer.name)) {
            return createForbidden(FORBIDDEN_MESSAGE)
        }
        val media = mediaController.findMediaById(mediaId)
        return if (media == null || media.customer?.id != customer.id) {
            createNotFound(NOT_FOUND_MESSAGE)
        } else createOk(mediaTranslator.translate(media))
    }

    override fun updateMedia(customerId: UUID, mediaId: UUID, media: Media): Response {
        val loggedUserId = loggedUserId!!
        val customer = customerController.findCustomerById(customerId)
            ?: return createNotFound(NOT_FOUND_MESSAGE)
        if (!isAdminOrHasCustomerGroup(customer.name)) {
            return createForbidden(FORBIDDEN_MESSAGE)
        }
        val foundMedia = mediaController.findMediaById(mediaId)
        return if (foundMedia == null || foundMedia.customer?.id != customer.id) {
            createNotFound(NOT_FOUND_MESSAGE)
        } else createOk(
            mediaTranslator.translate(
                mediaController.updateMedia(
                    foundMedia,
                    media.contentType,
                    media.type,
                    media.url,
                    loggedUserId
                )
            )
        )
    }

    override fun deleteMedia(customerId: UUID, mediaId: UUID): Response {
        val customer = customerController.findCustomerById(customerId) ?: return createNotFound(NOT_FOUND_MESSAGE)

        if (!isCustomerAdmin(customer.name)) {
            return createForbidden(FORBIDDEN_MESSAGE)
        }

        val media = mediaController.findMediaById(mediaId)
        if (media == null || media.customer?.id != customer.id) {
            return createNotFound(NOT_FOUND_MESSAGE)
        }

        mediaController.deleteMedia(media)
        return createNoContent()
    }

}