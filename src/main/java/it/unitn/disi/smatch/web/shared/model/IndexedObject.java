package it.unitn.disi.smatch.web.shared.model;

/**
 * An object with an index.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class IndexedObject {

    protected int index;

    public IndexedObject() {
        this.index = -1;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int newIndex) {
        index = newIndex;
    }
}
