package it.unitn.disi.smatch.web.shared.model.trees;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import it.unitn.disi.smatch.web.shared.model.IndexedObject;

import java.util.*;

/**
 * Base node class.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class BaseNode extends IndexedObject {

    @JsonBackReference("children")
    protected BaseNode parent;
    @JsonManagedReference("children")
    protected List<BaseNode> children;

    @JsonIgnore
    protected List<Object> listenerList;

    // id is needed to store nodeFormulas correctly.
    // nodeFormula is made of labelFormulas, each of which refers to tokens and tokens should have unique id
    // within a context. This is achieved by using node id + token id for each token
    protected String id;
    protected String name;

    @JsonIgnore
    protected Object userObject;

    // node counter to set unique node id during creation
    @JsonIgnore
    protected static long countNode = 0;

    // iterator which iterates over all parent nodes
    private final class AncestorsIterator implements Iterator<BaseNode> {
        private BaseNode current;

        public AncestorsIterator(BaseNode start) {
            if (null == start) {
                throw new IllegalArgumentException("argument is null");
            }
            this.current = start;
        }

        public boolean hasNext() {
            return current.hasParent();
        }

        public BaseNode next() {
            current = current.getParent();
            return current;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private class BreadthFirstSearchIterator implements Iterator<BaseNode> {
        private final LinkedList<BaseNode> queue;

        public BreadthFirstSearchIterator(BaseNode start) {
            if (null == start) {
                throw new IllegalArgumentException("start is required");
            }
            this.queue = new LinkedList<>();
            this.queue.addAll(start.getChildren());
        }

        public boolean hasNext() {
            return !queue.isEmpty();
        }

        public BaseNode next() {
            BaseNode current = queue.poll();
            if (null != current) {
                this.queue.addAll(current.getChildren());
            } else {
                throw new NoSuchElementException();
            }
            return current;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private class DepthFirstSearchIterator implements Iterator<BaseNode> {
        private final LinkedList<BaseNode> queue;

        public DepthFirstSearchIterator(BaseNode start) {
            if (null == start) {
                throw new IllegalArgumentException("start is required");
            }
            this.queue = new LinkedList<>();

            for (int i = start.getChildCount() - 1; 0 <= i; i--) {
                queue.addFirst(start.getChildAt(i));
            }
        }

        public boolean hasNext() {
            return !queue.isEmpty();
        }

        public BaseNode next() {
            BaseNode current = queue.poll();
            if (null != current) {
                for (int i = current.getChildCount() - 1; 0 <= i; i--) {
                    queue.addFirst(current.getChildAt(i));
                }

                return current;
            } else {
                throw new NoSuchElementException();
            }
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public BaseNode() {
        parent = null;
        children = null;
        listenerList = null;
        // need to set node id to keep track of concepts in c@node formulas
        // synchronized to make counts unique within JVM and decrease the chance of creating the same id
        synchronized (BaseNode.class) {
            id = "n" + countNode + "_" + ((System.currentTimeMillis() / 1000) % (365 * 24 * 3600));
            countNode++;
        }
        name = "";
    }

    /**
     * Constructor class which sets the node name.
     *
     * @param name the name of the node
     */
    public BaseNode(String name) {
        this();
        this.name = name;
    }

    @JsonIgnore
    public BaseNode getChildAt(int index) {
        if (children == null) {
            throw new ArrayIndexOutOfBoundsException("node has no children");
        }
        return children.get(index);
    }

    @JsonIgnore
    public int getChildCount() {
        if (children == null) {
            return 0;
        } else {
            return children.size();
        }
    }

    @JsonIgnore
    public int getChildIndex(BaseNode child) {
        if (null == child) {
            throw new IllegalArgumentException("argument is null");
        }

        if (!isChild(child)) {
            return -1;
        }
        return children.indexOf(child);
    }

    public Iterator<BaseNode> childrenIterator() {
        if (null == children) {
            return Collections.<BaseNode>emptyList().iterator();
        } else {
            return children.iterator();
        }
    }

    public List<BaseNode> getChildren() {
        if (null != children) {
            return Collections.unmodifiableList(children);
        } else {
            return Collections.emptyList();
        }
    }

    public void setChildren(List<BaseNode> children) {
        this.children = children;
    }

    public BaseNode createChild() {
        BaseNode child = new BaseNode();
        addChild(child);
        return child;
    }

    public BaseNode createChild(String name) {
        BaseNode child = new BaseNode(name);
        addChild(child);
        return child;
    }

    public void addChild(BaseNode child) {
        addChild(getChildCount(), child);
    }

    public void addChild(int index, BaseNode child) {
        if (null == child) {
            throw new IllegalArgumentException("new child is null");
        } else if (isAncestor(child)) {
            throw new IllegalArgumentException("new child is an ancestor");
        }

        BaseNode oldParent = child.getParent();

        if (null != oldParent) {
            oldParent.removeChild(child);
        }

        child.setParent(this);
        if (null == children) {
            children = new ArrayList<>();
        }
        children.add(index, child);
        fireTreeStructureChanged(this);
    }

    public void removeChild(int index) {
        BaseNode child = getChildAt(index);
        children.remove(index);
        fireTreeStructureChanged(this);
        child.setParent(null);
    }

    public void removeChild(BaseNode child) {
        if (null == child) {
            throw new IllegalArgumentException("argument is null");
        }

        if (isChild(child)) {
            removeChild(getChildIndex(child));
        }
    }

    public BaseNode getParent() {
        return parent;
    }

    public void setParent(BaseNode newParent) {
        removeFromParent();
        parent = newParent;
    }

    @JsonIgnore
    public boolean hasParent() {
        return null != parent;
    }

    public void removeFromParent() {
        if (null != parent) {
            parent.removeChild(this);
            parent = null;
        }
    }

    @JsonIgnore
    public boolean isLeaf() {
        return 0 == getChildCount();
    }

    public int ancestorCount() {
        int result = 0;
        if (null != parent) {
            result = parent.ancestorCount() + 1;
        }
        return result;
    }

    public Iterator<BaseNode> ancestorsIterator() {
        return new AncestorsIterator(this);
    }

    public int descendantCount() {
        int descendantCount = 0;
        for (Iterator<BaseNode> i = descendantsIterator(); i.hasNext(); ) {
            i.next();
            descendantCount++;
        }
        return descendantCount;
    }

    public Iterator<BaseNode> descendantsIterator() {
        return new DepthFirstSearchIterator(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        name = newName;
    }

    public String getId() {
        return id;
    }

    public void setId(String newId) {
        id = newId;
    }

    public Object getUserObject() {
        return userObject;
    }

    public void setUserObject(Object object) {
        userObject = object;
    }

    @Override
    public String toString() {
        return name;
    }

    public void addTreeStructureChangedListener(IBaseTreeStructureChangedListener<BaseNode> l) {
        if (null == listenerList) {
            listenerList = new ArrayList<>();
        }
        listenerList.add(l);
    }

    public void removeTreeStructureChangedListener(IBaseTreeStructureChangedListener<BaseNode> l) {
        if (null != listenerList) {
            listenerList.remove(l);
        }
    }

    @SuppressWarnings({"unchecked"})
    public void fireTreeStructureChanged(BaseNode node) {
        if (null != listenerList) {
            // Guaranteed to return a non-null array
            Object[] listeners = listenerList.toArray();
            // Process the listeners last to first, notifying
            // those that are interested in this event
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i + 1] instanceof IBaseTreeStructureChangedListener) {
                    // Lazily create the event:
                    ((IBaseTreeStructureChangedListener<BaseNode>) listeners[i + 1]).treeStructureChanged(node);
                }
            }
        }
        if (null != parent) {
            parent.fireTreeStructureChanged(node);
        }
    }

    private boolean isAncestor(BaseNode node) {
        if (null == node) {
            return false;
        }

        BaseNode ancestor = this;

        do {
            if (ancestor == node) {
                return true;
            }
        } while ((ancestor = ancestor.getParent()) != null);

        return false;
    }

    private boolean isChild(BaseNode node) {
        return null != node && 0 != getChildCount() && node.getParent() == this && -1 < children.indexOf(node);
    }
}