package it.unitn.disi.smatch.web.client.model;

public class MatchLog {
    private String source;
    private String relation;
    private String target;

    public String getSource() {
        return source;
    }

    public String getRelation() {
        return relation;
    }

    public String getTarget() {
        return target;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }
    public void setRelation(char relation) {
        this.relation = String.valueOf(relation);
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public MatchLog(String source, String relation, String target) {
        this.source = source;
        this.relation = relation;
        this.target = target;
    }
    public MatchLog() {

    }

}
