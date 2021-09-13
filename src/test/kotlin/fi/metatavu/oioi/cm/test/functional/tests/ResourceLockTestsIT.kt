package fi.metatavu.oioi.cm.test.functional.tests

import fi.metatavu.ikioma.integrations.test.functional.resources.MysqlResource
import fi.metatavu.oioi.cm.client.models.ResourceType
import fi.metatavu.oioi.cm.test.functional.builder.TestBuilder
import fi.metatavu.oioi.cm.test.functional.resources.KeycloakTestResource
import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

/**
 * Resource lock functional tests
 *
 * @author Jari Nykänen
 */
@QuarkusTest
@QuarkusTestResource.List(
    QuarkusTestResource(KeycloakTestResource::class),
    QuarkusTestResource(MysqlResource::class)
)
class ResourceLockTestsIT : AbstractFunctionalTest() {

    /**
     * Tests resource lock creation
     */
    @Test
    fun createLock() {
        TestBuilder().use { builder ->
            val customer = builder.admin().customers.create()
            val device = builder.admin().devices.create(customer)
            val application = builder.admin().applications.create(customer, device)
            val secondApplication = builder.admin().applications.create(customer, device)

            val resource = builder.admin().resources.create(
                customer = customer,
                device = device,
                application = application,
                orderNumber = 0,
                parentId = application.activeContentVersionResourceId,
                data = "data",
                name = "name",
                slug = "slug",
                ResourceType.mENU
            )

            val resourceLock = builder.admin().resources.updateResourceLock(
                application = application,
                customer = customer,
                device = device,
                resource = resource
            )

            assertNotNull(resourceLock)
            assertEquals(resourceLock.userDisplayName, "Admin User")
            assertEquals(resourceLock.userId, UUID.fromString("64763625-dda7-4983-b18c-6daa9e299ec4"))

            val secondResource = builder.admin().resources.create(
                customer = customer,
                device = device,
                application = application,
                orderNumber = 1,
                parentId = application.activeContentVersionResourceId,
                data = "data2",
                name = "name2",
                slug = "slug2",
                ResourceType.mENU
            )

            val resourceLockForAnotherUser = builder.user().resources.updateResourceLock(
                application = application,
                customer = customer,
                device = device,
                resource = secondResource
            )

            assertNotNull(resourceLockForAnotherUser)
            assertEquals(resourceLockForAnotherUser.userDisplayName, "user@example.com")
            assertEquals(resourceLockForAnotherUser.userId, UUID.fromString("6901afcc-435c-4780-93ba-9171cb604344"))

            val resourceInOtherApplication = builder.admin().resources.create(
                customer = customer,
                device = device,
                application = secondApplication,
                orderNumber = 1,
                parentId = secondApplication.activeContentVersionResourceId,
                data = "data3",
                name = "name3",
                slug = "slug3",
                ResourceType.mENU
            )

            val resourceLockForAnotherUserInOtherApplication = builder.admin().resources.updateResourceLock(
                application = secondApplication,
                customer = customer,
                device = device,
                resource = resourceInOtherApplication
            )

            assertNotNull(resourceLockForAnotherUserInOtherApplication)
            assertEquals(resourceLockForAnotherUserInOtherApplication.userDisplayName, "Admin User")
            assertEquals(resourceLockForAnotherUserInOtherApplication.userId, UUID.fromString("64763625-dda7-4983-b18c-6daa9e299ec4"))
        }
    }

    /**
     * Tests resource lock update
     */
    @Test
    fun updateLock() {
        TestBuilder().use { builder ->
            val customer = builder.admin().customers.create()
            val device = builder.admin().devices.create(customer)
            val application = builder.admin().applications.create(customer, device)

            val resource = builder.admin().resources.create(
                customer = customer,
                device = device,
                application = application,
                orderNumber = 0,
                parentId = application.activeContentVersionResourceId,
                data = "data",
                name = "name",
                slug = "slug",
                ResourceType.mENU
            )

            val resourceLock = builder.admin().resources.updateResourceLock(
                application = application,
                customer = customer,
                device = device,
                resource = resource
            )

            val updateLock = builder.admin().resources.updateResourceLock(
                application = application,
                customer = customer,
                device = device,
                resource = resource
            )

            assertNotNull(updateLock)
            assertEquals(updateLock.userDisplayName, "Admin User")
            assertEquals(updateLock.userId, UUID.fromString("64763625-dda7-4983-b18c-6daa9e299ec4"))
            assertEquals(resourceLock.userDisplayName, updateLock.userDisplayName)
            assertEquals(resourceLock.userId, updateLock.userId)
            assertNotEquals(resourceLock.expiresAt, updateLock.expiresAt)
        }
    }

    /**
     * Tests resource lock conflict
     */
    @Test
    fun updateLockConflict() {
        TestBuilder().use { builder ->
            val customer = builder.admin().customers.create()
            val device = builder.admin().devices.create(customer)
            val application = builder.admin().applications.create(customer, device)

            val resource = builder.admin().resources.create(
                customer = customer,
                device = device,
                application = application,
                orderNumber = 0,
                parentId = application.activeContentVersionResourceId,
                data = "data",
                name = "name",
                slug = "slug",
                ResourceType.mENU
            )

            builder.admin().resources.updateResourceLock(
                application = application,
                customer = customer,
                device = device,
                resource = resource
            )

            builder.user().resources.assertUpdateLockFailStatus(
                expectedStatus = 409,
                application = application,
                customer = customer,
                device = device,
                resource = resource
            )
        }
    }

    /**
     * Tests resource lock clear and update
     */
    @Test
    fun clearLockAndUpdate() {
        TestBuilder().use { builder ->
            val customer = builder.admin().customers.create()
            val device = builder.admin().devices.create(customer)
            val application = builder.admin().applications.create(customer, device)

            val resource = builder.admin().resources.create(
                customer = customer,
                device = device,
                application = application,
                orderNumber = 0,
                parentId = application.activeContentVersionResourceId,
                data = "data",
                name = "name",
                slug = "slug",
                ResourceType.mENU
            )

            builder.admin().resources.updateResourceLock(
                application = application,
                customer = customer,
                device = device,
                resource = resource
            )

            builder.user().resources.assertDeleteLockFailStatus(
                expectedStatus = 409,
                application = application,
                customer = customer,
                device = device,
                resource = resource
            )

            builder.admin().resources.deleteResourceLock(
                application = application,
                customer = customer,
                device = device,
                resource = resource
            )

            val resourceLock = builder.user().resources.updateResourceLock(
                application = application,
                customer = customer,
                device = device,
                resource = resource
            )

            assertNotNull(resourceLock)
            assertEquals(resourceLock.userDisplayName, "user@example.com")
            assertEquals(resourceLock.userId, UUID.fromString("6901afcc-435c-4780-93ba-9171cb604344"))
        }
    }

    /**
     * Tests list locked resource IDs
     */
    @Test
    fun listResourceLockIds() {
        TestBuilder().use { builder ->
            val customer = builder.admin().customers.create()
            val device = builder.admin().devices.create(customer)
            val application = builder.admin().applications.create(customer, device)
            val secondApplication = builder.admin().applications.create(customer, device)

            val emptyList = builder.user().resources.listLockedResourceIds(
                application = application,
                resource = null
            )

            assertEquals(0, emptyList.size)

            val resource = builder.admin().resources.create(
                customer = customer,
                device = device,
                application = application,
                orderNumber = 0,
                parentId = application.activeContentVersionResourceId,
                data = "data",
                name = "name",
                slug = "slug",
                ResourceType.mENU
            )

            val secondResource = builder.admin().resources.create(
                customer = customer,
                device = device,
                application = application,
                orderNumber = 1,
                parentId = application.activeContentVersionResourceId,
                data = "data2",
                name = "name2",
                slug = "slug2",
                ResourceType.mENU
            )

            builder.admin().resources.updateResourceLock(
                application = application,
                customer = customer,
                device = device,
                resource = resource
            )

            builder.admin().resources.updateResourceLock(
                application = application,
                customer = customer,
                device = device,
                resource = secondResource
            )

            builder.admin().resources.create(
                customer = customer,
                device = device,
                application = secondApplication,
                orderNumber = 0,
                parentId = secondApplication.activeContentVersionResourceId,
                data = "data",
                name = "name",
                slug = "slug",
                ResourceType.mENU
            )

            val resourceLocks = builder.user().resources.listLockedResourceIds(
                application = application,
                resource = null
            )

            assertEquals(2, resourceLocks.size)

            builder.admin().resources.deleteResourceLock(
                application = application,
                customer = customer,
                device = device,
                resource = resource
            )

            builder.admin().resources.deleteResourceLock(
                application = application,
                customer = customer,
                device = device,
                resource = secondResource
            )

            val resourceLocksAfterDelete = builder.user().resources.listLockedResourceIds(
                application = application,
                resource = null
            )

            assertEquals(0, resourceLocksAfterDelete.size)
        }
    }

    /**
     * Tests resource lock conflict
     */
    @Test
    fun findResourceLock() {
        TestBuilder().use { builder ->
            val customer = builder.admin().customers.create()
            val device = builder.admin().devices.create(customer)
            val application = builder.admin().applications.create(customer, device)

            val resource = builder.admin().resources.create(
                customer = customer,
                device = device,
                application = application,
                orderNumber = 0,
                parentId = application.activeContentVersionResourceId,
                data = "data",
                name = "name",
                slug = "slug",
                ResourceType.mENU
            )

            val createdLock = builder.admin().resources.updateResourceLock(
                application = application,
                customer = customer,
                device = device,
                resource = resource
            )

            val foundLock = builder.admin().resources.findResourceLock(
                application = application,
                customer = customer,
                device = device,
                resource = resource
            )

            assertNotNull(foundLock)
            assertEquals(foundLock.userDisplayName, "Admin User")
            assertEquals(foundLock.userId, UUID.fromString("64763625-dda7-4983-b18c-6daa9e299ec4"))
            assertEquals(createdLock.userDisplayName, foundLock.userDisplayName)
            assertEquals(createdLock.userId, foundLock.userId)
            assertEquals(createdLock.expiresAt, foundLock.expiresAt)

        }
    }

}