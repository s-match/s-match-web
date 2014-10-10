package it.unitn.disi.smatch.web.shared.model.mappings;

import it.unitn.disi.smatch.web.shared.model.trees.BaseNode;

/**
 * Mapping element implementation.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public class MappingElement {

    protected final BaseNode source;
    protected final BaseNode target;
    protected final char relation;

    public MappingElement(BaseNode source, BaseNode target, char relation) {
        this.source = source;
        this.target = target;
        this.relation = relation;
    }

    public BaseNode getSource() {
        return source;
    }

    public BaseNode getTarget() {
        return target;
    }

    public char getRelation() {
        return relation;
    }

    @Override
    public int hashCode() {
        int result;
        result = (source != null ? source.hashCode() : 0);
        result = 31 * result + (target != null ? target.hashCode() : 0);
        result = 31 * result + (int) relation;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (getClass() != o.getClass()) {
            return false;
        }

        MappingElement that = (MappingElement) o;

        if (relation != that.relation) {
            return false;
        }
        if (source != null ? !source.equals(that.source) : that.source != null) {
            return false;
        }
        if (target != null ? !target.equals(that.target) : that.target != null) {
            return false;
        }

        return true;
    }
}