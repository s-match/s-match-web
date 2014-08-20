package main.java.it.unitn.disi.smatch.web.shared.model.exceptions;

/**
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class ExceptionDetails {

    private int status;
    private String errorMessage;
    private String explanationMessage;
    private String whatToDoMessage;
    private String exceptionClass;
    private Object[] params;

    public ExceptionDetails() {
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getExceptionClass() {
        return exceptionClass;
    }

    public void setExceptionClass(String exceptionClass) {
        this.exceptionClass = exceptionClass;
    }

    public String getExplanationMessage() {
        return explanationMessage;
    }

    public void setExplanationMessage(String explanationMessage) {
        this.explanationMessage = explanationMessage;
    }

    public String getWhatToDoMessage() {
        return whatToDoMessage;
    }

    public void setWhatToDoMessage(String whatToDoMessage) {
        this.whatToDoMessage = whatToDoMessage;
    }

    @Override
    public String toString() {
        return status + " (" + errorMessage + ")";
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }
}