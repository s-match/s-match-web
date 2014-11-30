package it.unitn.disi.smatch.web.shared.model.exceptions;

/**
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
@HTTPResponseStatus(value = 400)
public class SMatchWebTaskTimedOutException extends SMatchWebException {

    public SMatchWebTaskTimedOutException() {
    }

    public SMatchWebTaskTimedOutException(String errorDescription) {
        super(errorDescription);
    }
}
