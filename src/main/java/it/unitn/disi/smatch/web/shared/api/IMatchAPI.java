package it.unitn.disi.smatch.web.shared.api;

import it.unitn.disi.smatch.web.shared.model.exceptions.SMatchWebException;
import it.unitn.disi.smatch.web.shared.model.tasks.MatchingTask;
import it.unitn.disi.smatch.web.shared.model.trees.BaseContextPair;

/**
 * Matching and task retrieval operations.
 *
 * @author <a rel="author" href="http://autayeu.com/">Aliaksandr Autayeu</a>
 * @spring-mvc-doclet.path match
 */
public interface IMatchAPI {

    /**
     * Matching operation.
     */
    final String MATCH = "match/{mode}";
    /**
     * Task retrieval operation.
     */
    final String TASK_READ = "tasks/{taskID}";
    /**
     * Demo contexts.
     */
    final String DEMO_CONTEXTS = "contexts/demo";

    /**
     * Default S-Match algorithm.
     */
    final String MODE_DEFAULT = "default";

    /**
     * Minimal S-Match algorithm.
     */
    final String MODE_MINIMAL = "minimal";

    /**
     * SPSM S-Match algorithm.
     */
    final String MODE_SPSM = "spsm";

    /**
     * Schedules the matching using specified configuration and returns task id.
     * Tasks time out (by default after 60 minutes).
     * Task results time out (by default after 10 minutes).
     *
     * @param contexts contexts to be matched
     * @param mode     match mode: default, minimal, spsm
     * @return matching task id
     * @throws SMatchWebException SMatchWebException
     */
    String match(BaseContextPair contexts, String mode) throws SMatchWebException;

    /**
     * Returns matching task by its id.
     *
     * @param taskID task id
     * @return matching task
     * @throws SMatchWebException SMatchWebException
     */
    MatchingTask readTask(String taskID) throws SMatchWebException;

    /**
     * Returns a demo pair of contexts.
     *
     * @return a demo pair of contexts
     */
    BaseContextPair demoContextPair();
}