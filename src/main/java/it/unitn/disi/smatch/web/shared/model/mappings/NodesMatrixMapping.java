package it.unitn.disi.smatch.web.shared.model.mappings;

import it.unitn.disi.smatch.web.shared.model.IndexedObject;
import it.unitn.disi.smatch.web.shared.model.trees.BaseContext;
import it.unitn.disi.smatch.web.shared.model.trees.BaseNode;

import java.util.Iterator;

/**
 * Mapping between context nodes based on a matrix.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class NodesMatrixMapping extends MatrixMapping {

    public NodesMatrixMapping(BaseContext source, BaseContext target) {
        super(source, target);
    }

    @Override
    protected int indexSource(BaseContext c) {
        return indexContext(c);
    }

    @Override
    protected int indexTarget(BaseContext c) {
        return indexContext(c);
    }

    /**
     * Indexes and counts nodes in the context
     *
     * @param c context
     * @return node count
     */
    public static int indexContext(BaseContext c) {
        int result = 0;
        for (Iterator<BaseNode> i = c.nodeIterator(); i.hasNext(); ) {
            BaseNode node = i.next();
            node.setIndex(result);
            result++;
        }
        return result;
    }

    @Override
    protected void initCols(BaseContext targetContext, IndexedObject[] targets) {
        initNodes(targetContext, targets);
    }

    @Override
    protected void initRows(BaseContext sourceContext, IndexedObject[] sources) {
        initNodes(sourceContext, sources);
    }

    private void initNodes(BaseContext c, IndexedObject[] o) {
        for (Iterator<BaseNode> i = c.nodeIterator(); i.hasNext(); ) {
            BaseNode node = i.next();
            o[node.getIndex()] = node;
        }
    }
}