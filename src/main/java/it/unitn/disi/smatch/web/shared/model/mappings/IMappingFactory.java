package main.java.it.unitn.disi.smatch.web.shared.model.mappings;

import main.java.it.unitn.disi.smatch.web.shared.model.trees.IBaseContext;
import main.java.it.unitn.disi.smatch.web.shared.model.trees.IBaseNode;

/**
 * Produces mappings.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public interface IMappingFactory {

    IContextMapping<IBaseNode> getContextMappingInstance(IBaseContext<IBaseNode> source, IBaseContext<IBaseNode> target);
}
