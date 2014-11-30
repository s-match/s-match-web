package it.unitn.disi.smatch.web.shared.model.exceptions;

/**
 * Root exception for S-Match web service.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class SMatchWebException extends Exception {

    public SMatchWebException() {
        super("");
    }

    public SMatchWebException(String errorDescription) {
        super(errorDescription);
    }
}