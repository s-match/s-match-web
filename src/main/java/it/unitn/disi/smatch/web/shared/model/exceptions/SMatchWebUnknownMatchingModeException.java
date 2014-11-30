package it.unitn.disi.smatch.web.shared.model.exceptions;

/**
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
@HTTPResponseStatus(value = 400)
public class SMatchWebUnknownMatchingModeException extends SMatchWebException {

    public SMatchWebUnknownMatchingModeException() {
    }

    public SMatchWebUnknownMatchingModeException(String errorDescription) {
        super(errorDescription);
    }
}
