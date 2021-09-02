package fi.metatavu.oioi.cm.persistence.dao

import org.slf4j.Logger
import java.lang.reflect.ParameterizedType
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.persistence.EntityManager
import javax.persistence.EntityNotFoundException
import javax.persistence.PersistenceContext
import javax.persistence.TypedQuery

/**
 * Abstract base class for all DAO classes
 *
 * @author Antti Leppä
 *
 * @param <T> entity type </T>
 **/
@ApplicationScoped
abstract class AbstractDAO<T> {

    @Inject
    lateinit var logger: Logger

    @PersistenceContext
    lateinit var entityManager: EntityManager

    private val genericTypeClass: Class<T?>?
        get() {
            val genericSuperclass = javaClass.genericSuperclass
            if (genericSuperclass is ParameterizedType) {
                return getFirstTypeArgument(genericSuperclass)
            } else {
                if ((genericSuperclass is Class<*>) && AbstractDAO::class.java.isAssignableFrom(genericSuperclass)) {
                    return getFirstTypeArgument(genericSuperclass.genericSuperclass as ParameterizedType)
                }
            }
            return null
        }

    /**
     * Returns entity by id
     *
     * @param id entity id
     * @return entity or null if non found
     */
    fun findById(id: UUID): T? {
        return entityManager.find(genericTypeClass, id)
    }

    /**
     * Returns entity by id
     *
     * @param id entity id
     * @return entity or null if non found
     */
    fun findById(id: String): T? {
        return entityManager.find(genericTypeClass, id)
    }

    /**
     * Returns entity by id
     *
     * @param id entity id
     * @return entity or null if non found
     */
    fun findById(id: Long?): T? {
        return entityManager.find(genericTypeClass, id)
    }

    /**
     * Lists all entities from database
     *
     * @return all entities from database
     */
    @Suppress("UNCHECKED_CAST")
    fun listAll(): List<T> {
        val genericTypeClass: Class<*>? = genericTypeClass
        val query = entityManager.createQuery("select o from " + genericTypeClass!!.name + " o")
        return query.resultList as List<T>
    }

    /**
     * Lists all entities from database limited by firstResult and maxResults parameters
     *
     * @param firstResult first result
     * @param maxResults max results
     * @return all entities from database limited by firstResult and maxResults parameters
     */
    @Suppress("UNCHECKED_CAST")
    fun listAll(firstResult: Int, maxResults: Int): List<T> {
        val genericTypeClass: Class<*>? = genericTypeClass
        val query = entityManager.createQuery("select o from " + genericTypeClass!!.name + " o")
        query.firstResult = firstResult
        query.maxResults = maxResults
        return query.resultList as List<T>
    }

    /**
     * Returns count of all entities
     *
     * @return entity count
     */
    fun count(): Long {
        val genericTypeClass: Class<*>? = genericTypeClass
        val query = entityManager.createQuery("select count(o) from " + genericTypeClass!!.name + " o")
        return query.singleResult as Long
    }

    /**
     * Deletes entity
     *
     * @param e entity
     */
    fun delete(e: T) {
        entityManager.remove(e)
        flush()
    }

    /**
     * Returns whether entity with given id exists
     *
     * @param id entity id
     * @return whether entity with given id exists
     */
    fun isExisting(id: Long?): Boolean {
        return try {
            entityManager.find(genericTypeClass, id) != null
        } catch (e: EntityNotFoundException) {
            false
        }
    }

    /**
     * Persists an entity
     *
     * @param object entity to be persisted
     * @return persisted entity
     */
    protected fun persist(`object`: T): T {
        entityManager.persist(`object`)
        return `object`
    }

    /**
     * Returns single result entity or null if result is empty
     *
     * @param query query
     * @return entity or null if result is empty
     */
    protected fun <X> getSingleResult(query: TypedQuery<X>): X? {
        val list = query.resultList
        if (list.isEmpty()) return null
        if (list.size > 1) {
            logger.error(
                String.format(
                    "SingleResult query returned %d elements from %s",
                    list.size,
                    genericTypeClass!!.name
                )
            )
        }
        return list[list.size - 1]
    }

    @Suppress("UNCHECKED_CAST")
    private fun getFirstTypeArgument(parameterizedType: ParameterizedType): Class<T?> {
        return parameterizedType.actualTypeArguments[0] as Class<T?>
    }

    /**
     * Flushes persistence context state
     */
    private fun flush() {
        entityManager.flush()
    }
}