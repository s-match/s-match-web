package it.unitn.disi.smatch.web.shared.model.trees;

/**
 * A pair of contexts for matching.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class BaseContextPair {

    /**
     * Source (left) context.
     */
    private BaseContext sourceContext;

    /**
     * Target (right) context.
     */
    private BaseContext targetContext;

    public BaseContext getSourceContext() {
        return sourceContext;
    }

    public void setSourceContext(BaseContext sourceContext) {
        this.sourceContext = sourceContext;
    }

    public BaseContext getTargetContext() {
        return targetContext;
    }

    public void setTargetContext(BaseContext targetContext) {
        this.targetContext = targetContext;
    }
}