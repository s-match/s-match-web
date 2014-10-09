package it.unitn.disi.smatch.web.shared.model.exceptions;

/**
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
@HTTPResponseStatus(value = 413)
public class SMatchWebNodeLimitException extends SMatchWebException {

    public SMatchWebNodeLimitException() {
    }

    public SMatchWebNodeLimitException(String errorDescription) {
        super(errorDescription);
    }
}
