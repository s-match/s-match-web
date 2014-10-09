package it.unitn.disi.smatch.web.shared.model.matrices;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.unitn.disi.smatch.data.mappings.IMappingElement;

import java.util.Arrays;

/**
 * Default matrix for matching results.
 *
 * @author Mikalai Yatskevich mikalai.yatskevich@comlab.ox.ac.uk
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class MatchMatrix {

    private final int x;
    private final int y;
    private final char[][] matrix;

    /**
     * Factory constructor.
     */
    public MatchMatrix() {
        this.x = 0;
        this.y = 0;
        this.matrix = null;
    }

    /**
     * Matrix instance constructor.
     *
     * @param x row count
     * @param y column count
     */
    @JsonCreator
    public MatchMatrix(@JsonProperty final int x, @JsonProperty final int y) {
        this.x = x;
        this.y = y;
        this.matrix = new char[x][y];
        for (char[] row : matrix) {
            Arrays.fill(row, IMappingElement.IDK);
        }
    }

    @JsonCreator
    public MatchMatrix(@JsonProperty final int x, @JsonProperty final int y, @JsonProperty char[][] matrix) {
        this.x = x;
        this.y = y;
        this.matrix = matrix;
    }

    public char get(final int x, final int y) {
        return matrix[x][y];
    }

    public boolean set(final int x, final int y, final char value) {
        boolean result = value == matrix[x][y];
        matrix[x][y] = value;
        return result;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public char[][] getMatrix() {
        return matrix;
    }
}