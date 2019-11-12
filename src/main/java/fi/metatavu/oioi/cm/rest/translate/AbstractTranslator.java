package fi.metatavu.oioi.cm.rest.translate;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Abstract translator class
 * 
 * @author Antti Leppä
 * @author Heikki Kurhinen
 */
public abstract class AbstractTranslator<E, R> {
  
  public abstract R translate(E entity);

  /**
   * Translates list of entities
   * 
   * @param entities list of entities to translate
   * @return List of translated entities
   */
  public List<R> translate(List<E> entities) {
    return entities.stream().map(this::translate).collect(Collectors.toList());
  }

}
