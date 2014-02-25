package it.unitn.disi.smatch.web.shared.model.trees;

/**
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class BaseContextPair<E extends IBaseNode> {

    // TODO AA find a way to share code with S-Match data packages

    private IBaseContext<E> sourceContext;
    private IBaseContext<E> targetContext;

    public IBaseContext<E> getSourceContext() {
        return sourceContext;
    }

    public void setSourceContext(IBaseContext<E> sourceContext) {
        this.sourceContext = sourceContext;
    }

    public IBaseContext<E> getTargetContext() {
        return targetContext;
    }

    public void setTargetContext(IBaseContext<E> targetContext) {
        this.targetContext = targetContext;
    }
}