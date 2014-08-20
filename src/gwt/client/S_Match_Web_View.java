package gwt.client;


import gwt.client.widget.S_Match_Web_UI;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dev.CompilerContext.Builder;
import com.google.gwt.dev.asm.Label;
import com.google.gwt.thirdparty.javascript.rhino.head.tools.debugger.Main;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class S_Match_Web_View implements EntryPoint {


	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

	    
	        
		S_Match_Web_UI p = new S_Match_Web_UI();
		
		RootPanel.get().add(p);
		
  }
}
