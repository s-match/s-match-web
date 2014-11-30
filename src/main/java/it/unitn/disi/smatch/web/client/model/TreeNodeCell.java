package it.unitn.disi.smatch.web.client.model;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import java.util.Set;

public class TreeNodeCell implements Cell<String> {

    public boolean dependsOnSelection() {
        // TODO Auto-generated method stub
        return false;
    }

    public Set<String> getConsumedEvents() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean handlesSelection() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isEditing(Context context,
                             Element parent, String value) {
        // TODO Auto-generated method stub
        return false;
    }

    public void onBrowserEvent(Context context,
                               Element parent, String value, NativeEvent event,
                               ValueUpdater<String> valueUpdater) {
        // TODO Auto-generated method stub

    }

    public void render(Context context,
                       String value, SafeHtmlBuilder sb) {
        // TODO Auto-generated method stub

    }

    public boolean resetFocus(Context context,
                              Element parent, String value) {
        // TODO Auto-generated method stub
        return false;
    }

    public void setValue(Context context,
                         Element parent, String value) {
        // TODO Auto-generated method stub

    }

}
