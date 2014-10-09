package it.unitn.disi.smatch.web.shared.model.mappings;

import it.unitn.disi.smatch.web.shared.model.trees.BaseContext;

/**
 * Base mapping class.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public abstract class BaseMapping {

    protected double similarity;
    protected final BaseContext sourceContext;
    protected final BaseContext targetContext;

    protected BaseMapping() {
        this.sourceContext = null;
        this.targetContext = null;
    }

    protected BaseMapping(final BaseContext sourceContext, final BaseContext targetContext) {
        this.sourceContext = sourceContext;
        this.targetContext = targetContext;
    }

    public double getSimilarity() {
        return similarity;
    }

    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }

    public BaseContext getSourceContext() {
        return sourceContext;
    }

    public BaseContext getTargetContext() {
        return targetContext;
    }
}
