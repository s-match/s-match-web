package gwt.client.widget;

import gwt.client.model.CustomTreeModel;
import gwt.client.model.MatchLog;
import gwt.client.model.TNode;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.SafeHtmlHeader;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.TreeNode;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.TreeViewModel;


public class S_Match_Web_UI extends Composite {

	private static paletteUiBinder uiBinder = GWT.create(paletteUiBinder.class);
	/**
	   * The CellTree.
	   */
	  @UiField(provided = true)
	  CellTree cellTreeSource;
	  
	  @UiField(provided = true)
	  CellTree cellTreeTarget;
	  
	  @UiField(provided = true)
	  CellTable<MatchLog> cellTable;      
	  @UiField Button savebtn;
	  @UiField Button createbtn;
	  @UiField TextArea  textTreeSource;
	  @UiField TextArea  textTreeTarget;
	  @UiField(provided = true)
	  ListBox cbConfig;
	  @UiField Button saveSourceTreeText;
	  @UiField Button saveTargetTreeText;
	  
	  @UiField Button addSourceNode;
	  @UiField Button addTargetNode;
	  @UiField Button addTargetChildNode;
	  @UiField Button deleteTargetNode;
	  @UiField Button addSourceChildNode;
	  @UiField Button deleteSourceNode;
	  @UiField TabPanel treeTargetOptionTabs;
	  @UiField TabPanel treeSourceOptionTabs;
	  MsgPopUp msg;
	  
	
	  
	interface paletteUiBinder extends UiBinder<Widget, S_Match_Web_UI> {
	}

	public S_Match_Web_UI() {
		
        
        
		createCellTrees();
		createCellTable();
		createMatchComboBox();
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	private void createMatchComboBox() {
		cbConfig = new ListBox();
        cbConfig.addItem("S-Match", "../conf/s-match.properties");
        cbConfig.addItem("S-Match Minimal", "../conf/s-match-minimal.properties");
        cbConfig.addItem("S-Match SPSM", "../conf/s-match-spsm.properties");
		
	}

	public void createCellTrees(){
		
		// Create a model for the tree.
	    TreeViewModel modelSource = new CustomTreeModel();
	    TreeViewModel modelTarget = new CustomTreeModel();
	    
	    
		cellTreeSource = new CellTree(modelSource,null);
		cellTreeTarget = new CellTree(modelTarget,null);
		
	}
public void createCellTable(){
	cellTable = new CellTable<MatchLog>();
	TextColumn<MatchLog> sourceColumn = new TextColumn<MatchLog>() {
	      @Override
	      public String getValue(MatchLog match) {
	        return match.getSource();
	      }
	    };
	    
	    //to algin the table title to center
	   sourceColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    // Make the name column sortable.
	    sourceColumn.setSortable(true);

	    TextColumn<MatchLog> relationColumn = new TextColumn<MatchLog>() {
		      @Override
		      public String getValue(MatchLog match) {
		        return match.getRelation();
		      }
		    };
		    relationColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		    // Make the name column sortable.
		    relationColumn.setSortable(true);
		    TextColumn<MatchLog> targetColumn = new TextColumn<MatchLog>() {
			      @Override
			      public String getValue(MatchLog match) {
			        return match.getTarget();
			      }
			    };
			    targetColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			    // Make the name column sortable.
			    targetColumn.setSortable(true);


	    // Add the columns.
			    
			    SafeHtmlHeader sourceHeader = new SafeHtmlHeader(new SafeHtml() { 

                    /**
					 * 
					 */
					private static final long serialVersionUID = 6404787005903497186L;

					public String asString() { 
                            return "<p style=\"text-align:center;\">Source</p>"; 
                    } 
            }); 
	    cellTable.addColumn(sourceColumn, sourceHeader);
	    
	    SafeHtmlHeader relationHeader = new SafeHtmlHeader(new SafeHtml() { 

            /**
			 * 
			 */
			private static final long serialVersionUID = 6404787005903497186L;

			public String asString() { 
                    return "<p style=\"text-align:center;\">Relation</p>"; 
            } 
    });
	    cellTable.addColumn(relationColumn, relationHeader);
	    
	    
	    SafeHtmlHeader targetHeader = new SafeHtmlHeader(new SafeHtml() { 

            /**
			 * 
			 */
			private static final long serialVersionUID = 6404787005903497186L;

			public String asString() { 
                    return "<p style=\"text-align:center;\">Target</p>"; 
            } 
    });
	    cellTable.addColumn(targetColumn, targetHeader);
}

	

	@UiHandler("saveSourceTreeText")
	void onSaveSourceTreeTextClick(ClickEvent event) {
		this.saveSourceTreeText.setText("Syncing...");
		this.saveSourceTreeText.setEnabled(false);
		refreshSourceTree();
		this.saveSourceTreeText.setText("Sync");
		this.saveSourceTreeText.setEnabled(true);
		this.treeSourceOptionTabs.selectTab(0,true);
		
	    
		
	}
	@UiHandler("saveTargetTreeText")
	void onSaveTargetTreeTextClick(ClickEvent event) {
		
		this.saveTargetTreeText.setText("Syncing...");
		this.saveTargetTreeText.setEnabled(false);
		refreshTargetTree();
		this.saveTargetTreeText.setText("Sync");
		this.saveTargetTreeText.setEnabled(true);
		this.treeTargetOptionTabs.selectTab(0,true);
		
	}
	
	void refreshSourceText(){
		this.textTreeSource.setVisible(false);
		//System.out.println("text tree is hiddden");
		CustomTreeModel model = (CustomTreeModel)this.cellTreeSource.getTreeViewModel();
		
		
		this.textTreeSource.setText(model.getAllNodeStrings());
		
		this.textTreeSource.setVisible(true);
		//System.out.println("text tree is visible");
	}
	
	void refreshSourceTree(){
		//this.cellTreeSource.setVisible(false);
		
		String ext_treeData = textTreeSource.getText();
		if(!validateText(ext_treeData)){
			return;
		}
		System.out.println("from text="+ext_treeData);
		ext_treeData = ext_treeData.trim();
		String[] treeNodes = ext_treeData.split(";");
		// Create a model for the tree.
	    CustomTreeModel model = (CustomTreeModel) this.cellTreeSource.getTreeViewModel();
	    model.clearAll(null);
	    for(int i=0; i< treeNodes.length;i++){
	    	System.out.println("node="+treeNodes[i]);
			if(treeNodes[i]!=""){
				String[] nodedata = treeNodes[i].trim().split(",");
				if(nodedata.length<2){
					System.out.println("Skipping line:"+treeNodes[i]+".");
					continue;
				}
				
				
				model.addNode(nodedata[1],nodedata[0]);
				
				
			}
			
		}
	    
	    //this.cellTreeSource = new CellTree(model,null);
	    model.refresh();
	    //System.out.println("Closing Tree Nodes");
	    //closeNodes(this.cellTreeSource.getRootTreeNode());
		
	    
	    System.out.println("Tree Ready");
	    
	    
	    //this.cellTreeSource.setVisible(true);
		
	}
	
	private void closeNodes(TreeNode tn) {
		if(tn==null) return;
		System.out.println("children for::"+tn.getChildCount());
		for(int childInd=0; childInd<tn.getChildCount();childInd++){
			TreeNode closedChildNode = tn.setChildOpen(childInd, false);
				closeNodes(closedChildNode);
			  
		  }
		
	}

	void refreshTargetText(){
		//this.textTreeTarget.setVisible(false);
		System.out.println("Target text tree is hiddden");
		CustomTreeModel model = (CustomTreeModel)this.cellTreeTarget.getTreeViewModel();
		
		this.textTreeTarget.setText(model.getAllNodeStrings());
		
		this.textTreeTarget.setVisible(true);
		System.out.println("Target text tree is visible");
	}
	
	void refreshTargetTree(){
		//this.cellTreeTarget.setVisible(false);
		
		String ext_treeData = textTreeTarget.getText();
		
		if(!validateText(ext_treeData)){
			return;
		}
		ext_treeData = ext_treeData.trim();
		String[] treeNodes = ext_treeData.split(";");
		// Create a model for the tree.
	    CustomTreeModel model = (CustomTreeModel) this.cellTreeTarget.getTreeViewModel();
	    model.clearAll(null);
	    for(int i=0; i< treeNodes.length;i++){
			if(treeNodes[i]!=""){
				String[] nodedata = treeNodes[i].trim().split(",");
				if(nodedata.length<2){
					System.out.println("Skipping line:"+treeNodes[i]+".");
					continue;
				}
				
				model.addNode(nodedata[1], nodedata[0]);
				
			}
			
		}
	    System.out.println("Target Tree Ready");
	    model.refresh();
	    //closeNodes(this.cellTreeTarget.getRootTreeNode());
	    //this.cellTreeTarget.setVisible(true);
		
	}
	
	@UiHandler("createSource")
	void onCreateSourceClick(ClickEvent event) {
		CustomTreeModel model = (CustomTreeModel) this.cellTreeSource.getTreeViewModel();
	    model.clearAll(null);
	    model.createDefault(null);
	    model.refresh();
	    closeNodes(this.cellTreeSource.getRootTreeNode());
		
		
	}
	@UiHandler("createTarget")
	void onCreateTargetClick(ClickEvent event) {
		CustomTreeModel model = (CustomTreeModel) this.cellTreeTarget.getTreeViewModel();
	    model.clearAll(null);
	    model.createDefault(null);
	    model.refresh();
	    closeNodes(this.cellTreeTarget.getRootTreeNode());
		
	}
	
	void showError(String errorStr){
		msg = new MsgPopUp(errorStr);
		msg.show();
	}
	
	@UiHandler("treeTargetOptionTabs")
	void onTreeTargetOptionTabsSelection(SelectionEvent<Integer>  event) {
		if (event.getSelectedItem() == 1) {
			addTargetNode.setEnabled(false);
			addTargetChildNode.setEnabled(false);
			deleteTargetNode.setEnabled(false);
			refreshTargetText();

		    }
		if (event.getSelectedItem() == 0) {
			addTargetNode.setEnabled(true);
			addTargetChildNode.setEnabled(true);
			deleteTargetNode.setEnabled(true);
			//refreshTargetTree();

		    }
		
	}
	@UiHandler("treeSourceOptionTabs")
	void onTreeSourceOptionTabsSelection(SelectionEvent<Integer> event) {
		//Tree area for Source selected
		if (event.getSelectedItem() == 0) {
			//System.out.println("0 selected");
			addSourceNode.setEnabled(true);
			addSourceChildNode.setEnabled(true);
			deleteSourceNode.setEnabled(true);
			//refreshSourceTree();
		    }
		//Text area for source is selected
		if (event.getSelectedItem() == 1) {
			//System.out.println("1 selected");
			addSourceNode.setEnabled(false);
			addSourceChildNode.setEnabled(false);
			deleteSourceNode.setEnabled(false);
			refreshSourceText();
		    }
	}
	@UiHandler("addSourceChildNode")
	void onAddSourceChildNodeClick(ClickEvent event) {
		
		CustomTreeModel model = (CustomTreeModel) this.cellTreeSource.getTreeViewModel();
		TNode selNode = model.getSelectionModel().getSelectedObject();
		if(selNode == null){
			showError("No node selected");
			return;
		}
		//TNode parentNode = selNode.getParent();
		System.out.println("selected node="+selNode);
		if(selNode!=null){
				model.createDefault(selNode);
				//selNode.addChild(null);
			
		}
		model.refresh();
	}
	@UiHandler("deleteSourceNode")
	void onDeleteSourceNodeClick(ClickEvent event) {
		CustomTreeModel model = (CustomTreeModel) this.cellTreeSource.getTreeViewModel();
		TNode selNode = model.getSelectionModel().getSelectedObject();
		
		if(selNode!=null){
			System.out.println("clearing children of "+selNode.getNodeLabel());
			if(selNode.getParent()!=null){
				selNode.clearAll();
				System.out.println("cleared, Now checking the parent("+selNode.getParent()+")");
//				
//				if((model.getNodeAt(model.getRoot(),null)).getNodeLabel().equalsIgnoreCase(selNode.getParent().getNodeLabel())){
//					System.out.println("Removing Node from under root");
//					model.removeNode(selNode);
//				}else{
					selNode.getParent().removeChild(selNode);
//				}
				System.out.println(selNode.getNodeLabel()+" Removed from tree.");
			}else{
				showError("Cannot delete Root.");
			}
		}
		//System.out.println("----------------------");
		//model.getSelectionModel().clear();
		model.refresh();
	}
	@UiHandler("addTargetChildNode")
	void onAddTargetChildNodeClick(ClickEvent event) {

		CustomTreeModel model = (CustomTreeModel) this.cellTreeTarget.getTreeViewModel();
		TNode selNode = model.getSelectionModel().getSelectedObject();
		
		if(selNode!=null){
			
				model.createDefault(selNode);
			
		}
		model.refresh();
	}
	@UiHandler("deleteTargetNode")
	void onDeleteTargetNodeClick(ClickEvent event) {
		CustomTreeModel model = (CustomTreeModel) this.cellTreeTarget.getTreeViewModel();
		TNode selNode = model.getSelectionModel().getSelectedObject();
		System.out.println("selected Node="+selNode.toString());
		if(selNode!=null){
			selNode.clearAll();
			if(selNode.getParent()!=null){
				selNode.getParent().removeChild(selNode);
			}else{
				showError("Cannot delete Root.");
			}
		}
		//model.getSelectionModel().clear();
		model.refresh();
	}
	
	@UiHandler("addSourceNode")
	void onAddSourceNodeClick(ClickEvent event) {
		CustomTreeModel model = (CustomTreeModel) this.cellTreeSource.getTreeViewModel();
		TNode selNode = model.getSelectionModel().getSelectedObject();
		if(selNode!=null){
			TNode parentNode = selNode.getParent();
			if(parentNode==null){
				showError("Cannot add a sibling to the root.");
			}else{
				
				model.createDefault(parentNode);
			}
		}
		model.refresh();
		
	}
	private boolean validateText(String ext_treeData) {
		// TODO Auto-generated method stub
		
		return true;
	}

	@UiHandler("addTargetNode")
	void onAddTargetNodeClick(ClickEvent event) {
		CustomTreeModel model = (CustomTreeModel) this.cellTreeTarget.getTreeViewModel();
		TNode selNode = model.getSelectionModel().getSelectedObject();
		
		if(selNode!=null){
			TNode parentNode = selNode.getParent();
			if(parentNode==null){
				showError("Cannot add a sibling to the root.");
			}else{
				model.createDefault(parentNode);
			}
		}
		model.refresh();
	}
	
}

