package it.unitn.disi.smatch.web.shared.model.exceptions;

/**
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
@HTTPResponseStatus(value = 404)
public class SMatchWebTaskNotFoundException extends SMatchWebException {

    public SMatchWebTaskNotFoundException() {
    }

    public SMatchWebTaskNotFoundException(String errorDescription) {
        super(errorDescription);
    }
}