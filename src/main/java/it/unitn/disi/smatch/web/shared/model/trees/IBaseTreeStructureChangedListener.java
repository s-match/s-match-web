package it.unitn.disi.smatch.web.shared.model.trees;

import java.util.EventListener;

/**
 * Base listener interface.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public interface IBaseTreeStructureChangedListener<E extends BaseNode> extends EventListener {

    /**
     * Receives the signal that the structure of the <code>node</code> has been changed. That is, a child node has been
     * added or deleted.
     *
     * @param node the root of the changed tree
     */
    void treeStructureChanged(E node);
}
