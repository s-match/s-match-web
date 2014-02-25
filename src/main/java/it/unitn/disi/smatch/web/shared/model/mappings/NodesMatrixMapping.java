package it.unitn.disi.smatch.web.shared.model.mappings;

import it.unitn.disi.smatch.web.shared.model.IIndexedObject;
import it.unitn.disi.smatch.web.shared.model.trees.IBaseContext;
import it.unitn.disi.smatch.web.shared.model.trees.IBaseNode;

/**
 * Mapping between context nodes based on a matrix.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class NodesMatrixMapping extends MatrixMapping<IBaseNode> {

    public NodesMatrixMapping(IBaseContext<IBaseNode> source, IBaseContext<IBaseNode> target) {
        super(source, target);
    }

    @Override
    protected int getRowCount(IBaseContext<IBaseNode> c) {
        return getNodeCount(c);
    }

    @Override
    protected int getColCount(IBaseContext<IBaseNode> c) {
        return getNodeCount(c);
    }

    private int getNodeCount(IBaseContext<IBaseNode> c) {
        int result = 0;
        for (IBaseNode node : c.getNodesList()) {
            node.setIndex(result);
            result++;
        }
        return result;
    }

    @Override
    protected void initCols(IBaseContext<IBaseNode> targetContext, IIndexedObject[] targets) {
        initNodes(targetContext, targets);
    }

    @Override
    protected void initRows(IBaseContext<IBaseNode> sourceContext, IIndexedObject[] sources) {
        initNodes(sourceContext, sources);
    }

    private void initNodes(IBaseContext<IBaseNode> c, IIndexedObject[] o) {
        for (IBaseNode node : c.getNodesList()) {
            o[node.getIndex()] = node;
        }
    }
}