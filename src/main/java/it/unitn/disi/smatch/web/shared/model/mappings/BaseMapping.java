package main.java.it.unitn.disi.smatch.web.shared.model.mappings;

import main.java.it.unitn.disi.smatch.web.shared.model.trees.IBaseContext;
import main.java.it.unitn.disi.smatch.web.shared.model.trees.IBaseNode;

import java.util.AbstractSet;

/**
 * Base mapping class.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public abstract class BaseMapping<T> extends AbstractSet<IMappingElement<T>> implements IContextMapping<T> {

    protected double similarity;
    protected IBaseContext<IBaseNode> sourceContext;
    protected IBaseContext<IBaseNode> targetContext;

    public double getSimilarity() {
        return similarity;
    }

    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }

    public IBaseContext<IBaseNode> getSourceContext() {
        return sourceContext;
    }

    public IBaseContext<IBaseNode> getTargetContext() {
        return targetContext;
    }
}
