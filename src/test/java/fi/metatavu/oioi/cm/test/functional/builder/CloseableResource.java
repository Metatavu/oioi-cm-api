package fi.metatavu.oioi.cm.test.functional.builder;

/**
 * Describes closeable resource
 * 
 * @author Antti Leppä
 *
 * @param <T> entity class
 */
public abstract class CloseableResource<T> implements AutoCloseable {

  private T resource;

  /**
   * Constructor
   * 
   * @param resource resource
   */
  public CloseableResource(T resource) {
    super();
    this.resource = resource;
  }
  
  /**
   * Returns resource
   * 
   * @return resource
   */
  public T getResource() {
    return resource;
  }

}
