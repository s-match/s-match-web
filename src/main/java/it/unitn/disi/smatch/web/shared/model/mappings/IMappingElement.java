package it.unitn.disi.smatch.web.shared.model.mappings;

/**
 * Interface for a mapping element.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 */
public interface IMappingElement {

    // relations abbreviations
    char EQUIVALENCE = '=';
    char LESS_GENERAL = '<';
    char MORE_GENERAL = '>';
    char DISJOINT = '!';

    // relations for minimal links
    char ENTAILED_LESS_GENERAL = 'L';
    char ENTAILED_MORE_GENERAL = 'M';
    char ENTAILED_DISJOINT = 'X';

    char IDK = '?';
}