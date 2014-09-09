package gwt.client.model;

import com.google.gwt.user.cellview.client.CellTable;

public interface CustomTableResource extends CellTable.Resources {
	public interface CustomTableStyle extends CellTable.Style {};
	
	@Source({"CellTable.css"})
	CustomTableStyle cellTableStyle();

}
