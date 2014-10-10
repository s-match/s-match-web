package it.unitn.disi.smatch.web.shared.model.tasks;

import it.unitn.disi.smatch.web.shared.model.mappings.NodesMatrixMapping;

import java.util.Date;

/**
 * Matching task.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class MatchingTask {

    /**
     * Task id
     */
    private String id;

    /**
     * Matching result
     */
    private NodesMatrixMapping result;

    /**
     * Task status: DONE, PENDING, STARTED
     */
    private String status;

    /**
     * Task progress in percents
     */
    private Double progress;

    /**
     * Task submit time
     */
    private Date submitTime;

    /**
     * Task start time
     */
    private Date startTime;

    /**
     * Task complete time
     */
    private Date completeTime;

    /**
     * Task error message
     */
    private String errorMessage;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public NodesMatrixMapping getResult() {
        return result;
    }

    public void setResult(NodesMatrixMapping result) {
        this.result = result;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getProgress() {
        return progress;
    }

    public void setProgress(Double progress) {
        this.progress = progress;
    }

    public Date getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(Date completeTime) {
        this.completeTime = completeTime;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}