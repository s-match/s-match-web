package gwt.client.model;

import java.util.Set;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

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

	public boolean isEditing(com.google.gwt.cell.client.Cell.Context context,
			Element parent, String value) {
		// TODO Auto-generated method stub
		return false;
	}

	public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context,
			Element parent, String value, NativeEvent event,
			ValueUpdater<String> valueUpdater) {
		// TODO Auto-generated method stub

	}

	public void render(com.google.gwt.cell.client.Cell.Context context,
			String value, SafeHtmlBuilder sb) {
		// TODO Auto-generated method stub

	}

	public boolean resetFocus(com.google.gwt.cell.client.Cell.Context context,
			Element parent, String value) {
		// TODO Auto-generated method stub
		return false;
	}

	public void setValue(com.google.gwt.cell.client.Cell.Context context,
			Element parent, String value) {
		// TODO Auto-generated method stub

	}

}
