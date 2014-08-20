package main.java.it.unitn.disi.smatch.web.client.widget;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MsgPopUp {
	final DialogBox DBTError;
	
	public MsgPopUp(String errStr){
		
		DBTError = createErrorDialogBox(errStr);
		DBTError.setGlassEnabled(true);
		DBTError.setAnimationEnabled(true);
		DBTError.setStyleName("error");
		
	}

	public DialogBox getDBTError() {
		return DBTError;
	}

	public void show(){
		hide();
		DBTError.center();
		DBTError.show();
	}
	public void hide(){
		DBTError.hide();
	}

/**
 * Create the dialog box for Error.
 *
 * @return the new dialog box
 */
private DialogBox createErrorDialogBox(String errStr){

	final DialogBox dialogBox = new DialogBox();
	VerticalPanel vPanel = new VerticalPanel();
	dialogBox.setWidget(vPanel);
	
	
	 // Add a label
	    vPanel.add(new HTML("<p>"+errStr+"</p>"));
        
	 // Add a close button at the bottom of the dialog
	    Button closeButton = new Button("close");
	    closeButton.addClickHandler(new ClickHandler() {
	          public void onClick(ClickEvent event) {
	            dialogBox.hide();
	          }
	        });
	    closeButton.addStyleName("button");
	    
	      vPanel.add(new HTML("<br/>"));
	      vPanel.add(closeButton);
	      // Return the layout panel
	      return dialogBox;
}



}
