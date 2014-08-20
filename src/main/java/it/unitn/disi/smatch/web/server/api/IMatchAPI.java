package main.java.it.unitn.disi.smatch.web.server.api;

import main.java.it.unitn.disi.smatch.SMatchException;
import main.java.it.unitn.disi.smatch.data.mappings.IContextMapping;
import main.java.it.unitn.disi.smatch.data.trees.INode;
import main.java.it.unitn.disi.smatch.web.shared.model.trees.BaseContextPair;

/**
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 * @spring-mvc-doclet.path /
 */
public interface IMatchAPI {

    final String MATCH = "match";

    /**
     * Performs the whole matching process.
     *
     * @param contexts contexts to be matched
     * @return interface to resulting mapping
     * @throws it.unitn.disi.smatch.SMatchException SMatchException
     */
    IContextMapping<INode> match(BaseContextPair contexts) throws SMatchException;
}
