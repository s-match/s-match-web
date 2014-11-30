package it.unitn.disi.smatch.web.client.model;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * A custom {@link Cell} used to render an image and a string.
 */

public class RelationCell extends AbstractCell<String> {


    @Override
    public void render(Context context, String value, SafeHtmlBuilder sb) {
          /*
	       * Always do a null check on the value. Cell widgets can pass null to
	       * cells if the underlying data contains a null, or if the data arrives
	       * out of order.
	       */
        if (value == null) {
            return;
        }

        // Use the template to create the Cell's html.
        sb.appendHtmlConstant("<div class=\"" + value.toLowerCase() + "\">" + value + "</div>");
    }

}
