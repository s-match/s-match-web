package it.unitn.disi.smatch.web.client.model;

/**
 * Created by Ahmed on 11/23/2014.
 */
public interface ServerTaskOpener{
        void progressComplete (String json);
        void progressHalted ();
}
