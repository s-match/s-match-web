package it.unitn.disi.smatch.web.shared.model.mappings;

import it.unitn.disi.smatch.web.shared.model.trees.IBaseContext;

/**
 * Interface for context mappings.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public interface IContextMapping<T> extends IMapping<T> {

    IBaseContext getSourceContext();

    IBaseContext getTargetContext();
}
