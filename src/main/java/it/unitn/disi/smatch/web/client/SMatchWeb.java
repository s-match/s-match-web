package it.unitn.disi.smatch.web.client;

import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import it.unitn.disi.smatch.web.client.widget.S_Match_Web_UI;
import it.unitn.disi.smatch.web.shared.model.tasks.MatchingTask;
import it.unitn.disi.smatch.web.shared.model.trees.BaseContextPair;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class SMatchWeb implements EntryPoint {

    public static interface BaseContextPairMapper extends ObjectMapper<BaseContextPair> {
    }

    public static interface MatchingTaskMapper extends ObjectMapper<MatchingTask> {
    }

    /**
     * The message displayed to the user when the server cannot be reached or
     * returns an error.
     */
    private static final String SERVER_ERROR = "An error occurred while "
            + "attempting to contact the server. Please check your network "
            + "connection and try again.";

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        /**
         * This is the entry point method.
         */

        S_Match_Web_UI p = new S_Match_Web_UI();

        RootPanel.get().add(p);

    }
}
