package it.unitn.disi.smatch.web.shared.model.trees;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Collections;
import java.util.Iterator;

/**
 * Base class for contexts.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
@SuppressWarnings({"unchecked"})
public class BaseContext implements IBaseTreeStructureChangedListener<BaseNode> {

    protected BaseNode root;

    public BaseContext() {
        root = null;
    }

    public void setRoot(BaseNode root) {
        this.root = root;
        root.addTreeStructureChangedListener(this);
    }

    public BaseNode getRoot() {
        return root;
    }

    @JsonIgnore
    public boolean hasRoot() {
        return null != root;
    }

    public BaseNode createNode() {
        return new BaseNode();
    }

    public BaseNode createNode(String name) {
        return new BaseNode(name);
    }

    public BaseNode createRoot() {
        root = new BaseNode();
        root.addTreeStructureChangedListener(this);
        return root;
    }

    public BaseNode createRoot(String name) {
        BaseNode result = createRoot();
        result.setName(name);
        return result;
    }

    public Iterator<BaseNode> nodeIterator() {
        if (hasRoot()) {
            return new StartIterator<>(root, root.descendantsIterator());
        } else {
            return Collections.<BaseNode>emptyList().iterator();
        }
    }

    public int nodesCount() {
        if (null == root) {
            return 0;
        } else {
            return root.descendantCount() + 1;
        }
    }

    @Override
    public void treeStructureChanged(BaseNode node) {
    }
}