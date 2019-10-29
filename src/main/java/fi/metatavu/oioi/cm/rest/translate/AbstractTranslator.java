package fi.metatavu.oioi.cm.rest.translate;

/**
 * Abstract translator class
 * 
 * @author Antti Leppä
 */
public abstract class AbstractTranslator<E, R> {
  
  public abstract R translate(E entity);

}
