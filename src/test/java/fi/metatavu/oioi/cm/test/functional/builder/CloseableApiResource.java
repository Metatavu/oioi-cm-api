package fi.metatavu.oioi.cm.test.functional.builder;

/**
 * Describes API closeable resource
 * 
 * @author Antti Leppä
 *
 * @param <T> entity class
 * @param <A> API class
 */
public class CloseableApiResource<T, A> extends CloseableResource <T> {

  private AbstractApiTestBuilderResource<T, A> builder;

  /**
   * Constructor
   * 
   * @param builder resource builder
   * @param resource resource
   */
  public CloseableApiResource(AbstractApiTestBuilderResource<T, A> builder, T resource) {
    super(resource);
    this.builder = builder;
  }
  
  /**
   * Returns builder
   * 
   * @return builder
   */
  public AbstractApiTestBuilderResource<T, A> getBuilder() {
    return builder;
  }
  
  @Override
  public void close() throws Exception {
    builder.clean(getResource());
  }

}
