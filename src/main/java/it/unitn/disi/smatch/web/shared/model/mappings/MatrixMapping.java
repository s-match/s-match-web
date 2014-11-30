package it.unitn.disi.smatch.web.shared.model.mappings;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.unitn.disi.smatch.web.shared.model.IndexedObject;
import it.unitn.disi.smatch.web.shared.model.matrices.MatchMatrix;
import it.unitn.disi.smatch.web.shared.model.trees.BaseContext;
import it.unitn.disi.smatch.web.shared.model.trees.BaseNode;

import java.util.*;

/**
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class MatrixMapping extends BaseMapping {

    protected final MatchMatrix matrix;

    // for set size();
    private int elementCount;

    @JsonIgnore
    private BaseNode[] sources;
    @JsonIgnore
    private BaseNode[] targets;

    @JsonIgnore
    private volatile transient int modCount;

    private final class MatrixMappingIterator implements Iterator<MappingElement> {

        private int expectedModCount;
        private int curRow;
        private int curCol;
        private MappingElement next;
        private MappingElement current;

        private MatrixMappingIterator() {
            this.expectedModCount = modCount;
            if (0 == size()) {
                next = null;
            } else {
                curRow = -1;
                curCol = matrix.getY() - 1;
                next = findNext();
            }
        }

        public boolean hasNext() {
            return null != next;
        }

        public MappingElement next() {
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
            if (null == next) {
                throw new NoSuchElementException();
            }

            current = next;
            next = findNext();
            return current;
        }

        public void remove() {
            if (null == current) {
                throw new IllegalStateException();
            }
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
            setRelation(current.getSource().getIndex(), current.getTarget().getIndex(), IMappingElement.IDK);
            expectedModCount = modCount;
            current = null;
        }

        private MappingElement findNext() {
            MappingElement result = null;
            char relation = IMappingElement.IDK;
            do {
                curCol++;
                if (matrix.getY() == curCol) {
                    curRow++;
                    curCol = 0;
                }
            }
            while (curRow < matrix.getX() && curCol < matrix.getY() && IMappingElement.IDK == (relation = matrix.get(curRow, curCol)));

            if (IMappingElement.IDK != relation) {
                result = new MappingElement(sources[curRow], targets[curCol], relation);
            }
            return result;
        }
    }

    public MatrixMapping() {
        this.matrix = new MatchMatrix();
    }

    public MatrixMapping(BaseContext sourceContext, BaseContext targetContext) {
        super(sourceContext, targetContext);

        // counts and indexes them
        int rows = indexSource(sourceContext);
        int cols = indexTarget(targetContext);
        this.matrix = new MatchMatrix(rows, cols);

        this.sources = new BaseNode[rows];
        this.targets = new BaseNode[cols];

        initRows(sourceContext, sources);
        initCols(targetContext, targets);

        this.elementCount = 0;
        this.modCount = 0;
    }

    @JsonCreator
    public MatrixMapping(@JsonProperty("sourceContext") BaseContext sourceContext,
                         @JsonProperty("targetContext") BaseContext targetContext,
                         @JsonProperty("matrix") MatchMatrix matrix) {
        super(sourceContext, targetContext);

        // counts and indexes them
        int rows = indexSource(sourceContext);
        int cols = indexTarget(targetContext);
        this.matrix = matrix;

        this.sources = new BaseNode[rows];
        this.targets = new BaseNode[cols];

        initRows(sourceContext, sources);
        initCols(targetContext, targets);

        this.elementCount = 0;
        this.modCount = 0;
    }

    public MatchMatrix getMatrix() {
        return matrix;
    }

    protected void initCols(BaseContext targetContext, IndexedObject[] targets) {
        // void
    }

    protected void initRows(BaseContext sourceContext, IndexedObject[] sources) {
        // void
    }

    public char getRelation(IndexedObject source, IndexedObject target) {
        return matrix.get(source.getIndex(), target.getIndex());
    }

    public boolean setRelation(final int source, final int target, final char relation) {
        final boolean result = relation == matrix.get(source, target);

        if (!result) {
            modCount++;
            matrix.set(source, target, relation);
            if (IMappingElement.IDK == relation) {
                elementCount--;
            } else {
                elementCount++;
            }
        }

        return !result;
    }

    public Set<MappingElement> getSources(final BaseNode source) {
        final int sIdx = source.getIndex();
        Set<MappingElement> result = Collections.emptySet();
        if (0 <= sIdx && sIdx < sources.length && (source == sources[sIdx])) {
            result = new HashSet<>();
            for (int j = 0; j < targets.length; j++) {
                char rel = matrix.get(sIdx, j);
                if (IMappingElement.IDK != rel) {
                    result.add(new MappingElement(sources[sIdx], targets[j], rel));
                }
            }

        }
        return result;
    }

    public Set<MappingElement> getTargets(BaseNode target) {
        final int tIdx = target.getIndex();
        Set<MappingElement> result = Collections.emptySet();
        if (0 <= tIdx && tIdx < targets.length && (target == targets[tIdx])) {
            result = new HashSet<>();
            for (int i = 0; i < sources.length; i++) {
                char rel = matrix.get(i, tIdx);
                if (IMappingElement.IDK != rel) {
                    result.add(new MappingElement(sources[i], targets[tIdx], rel));
                }
            }
        }
        return result;
    }

    public int size() {
        return elementCount;
    }

    @JsonIgnore
    public boolean isEmpty() {
        return 0 == elementCount;
    }

    public boolean contains(Object o) {
        boolean result = false;
        if (o instanceof MappingElement) {
            final MappingElement e = (MappingElement) o;
            if (e.getSource() != null) {
                final IndexedObject s = e.getSource();
                if (e.getTarget() != null) {
                    final IndexedObject t = e.getTarget();
                    result = IMappingElement.IDK != getRelation(s, t) && s == sources[s.getIndex()] && t == targets[t.getIndex()];
                }
            }
        }
        return result;
    }

    public Iterator<MappingElement> iterator() {
        return new MatrixMappingIterator();
    }

    public boolean add(MappingElement e) {
        return setRelation(e.getSource().getIndex(), e.getTarget().getIndex(), e.getRelation());
    }

    public boolean remove(Object o) {
        boolean result = false;
        if (o instanceof MappingElement) {
            MappingElement e = (MappingElement) o;
            if (e.getSource() != null) {
                final IndexedObject s = e.getSource();
                if (e.getTarget() != null) {
                    final IndexedObject t = e.getTarget();
                    result = setRelation(s.getIndex(), t.getIndex(), IMappingElement.IDK);
                }
            }
        }

        return result;
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    protected int indexTarget(BaseContext c) {
        return -1;
    }

    protected int indexSource(BaseContext c) {
        return -1;
    }
}