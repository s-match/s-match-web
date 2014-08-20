package main.java.it.unitn.disi.smatch.web.shared.model.matrices;

/**
 * An interface to a matrix with matching results.
 *
 * @author Mikalai Yatskevich mikalai.yatskevich@comlab.ox.ac.uk
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public interface IMatchMatrix {

    /**
     * Inits a matrix x rows per y columns.
     *
     * @param x rows count
     * @param y column count
     */
    void init(int x, int y);

    /**
     * Returns an element.
     *
     * @param x row
     * @param y column
     * @return an element value
     */
    char get(int x, int y);

    /**
     * Sets an element.
     *
     * @param x     row
     * @param y     column
     * @param value a new element value
     * @return true if matrix was modified 
     */
    boolean set(int x, int y, char value);

    /**
     * Returns row count.
     *
     * @return row count
     */
    int getX();

    /**
     * Returns column count.
     *
     * @return column count
     */
    int getY();
}