package it.unitn.disi.smatch.web.shared.model.mappings;

import it.unitn.disi.smatch.web.shared.model.trees.IBaseContext;
import it.unitn.disi.smatch.web.shared.model.trees.IBaseNode;

/**
 * Produces mappings.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public interface IMappingFactory {

    IContextMapping<IBaseNode> getContextMappingInstance(IBaseContext<IBaseNode> source, IBaseContext<IBaseNode> target);
}
