package gwt.client.widget;

import java.util.ArrayList;

import gwt.client.model.CustomTableResource;
import gwt.client.model.CustomTreeModel;
import gwt.client.model.MatchLog;
import gwt.client.model.RelationCell;
import gwt.client.model.TNode;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.ImageCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.Column;
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
	@UiField
	Button savebtn;
	@UiField
	Button createbtn;
	@UiField
	TextArea textTreeSource;
	@UiField
	TextArea textTreeTarget;
	@UiField(provided = true)
	ListBox cbConfig;
	@UiField
	Button saveSourceTreeText;
	@UiField
	Button saveTargetTreeText;

	@UiField
	Button addSourceNode;
	@UiField
	Button addTargetNode;
	@UiField
	Button addTargetChildNode;
	@UiField
	Button deleteTargetNode;
	@UiField
	Button addSourceChildNode;
	@UiField
	Button deleteSourceNode;
	@UiField
	TabPanel treeTargetOptionTabs;
	@UiField
	TabPanel treeSourceOptionTabs;
	MsgPopUp msg;

	interface paletteUiBinder extends UiBinder<Widget, S_Match_Web_UI> {
	}

	public S_Match_Web_UI() {

		/*
		 * Create the source and target trees
		 */
		createCellTrees();
		/*
		 * Create the table
		 */
		createCellTable();
		
		/*
		 * Create and fill the matching options
		 */
		createMatchComboBox();
		
		/*
		 * initialize everything else, through the xml file.
		 */
		initWidget(uiBinder.createAndBindUi(this));
		
		/*
		 * Everything exists, so now we can refresh the textareas for source and target
		 * To have the default data in them.
		 */
		refreshSourceText();
		refreshTargetText();
	}

	private void createMatchComboBox() {
		cbConfig = new ListBox();
		cbConfig.addItem("S-Match", "../conf/s-match.properties");
		cbConfig.addItem("S-Match Minimal",
				"../conf/s-match-minimal.properties");
		cbConfig.addItem("S-Match SPSM", "../conf/s-match-spsm.properties");

	}

	public void createCellTrees() {

		// Create a model for the tree.
		TreeViewModel modelSource = new CustomTreeModel();
		TreeViewModel modelTarget = new CustomTreeModel();

		cellTreeSource = new CellTree(modelSource, null);
		
		cellTreeTarget = new CellTree(modelTarget, null);
		

	}

	public void createCellTable() {
		
		CellTable.Resources resource = GWT.create(CustomTableResource.class);
		cellTable = new CellTable<MatchLog>(1,resource);
		
		/*
		 * Source cell column
		 */
		TextColumn<MatchLog> sourceColumn = new TextColumn<MatchLog>() {
			@Override
			public String getValue(MatchLog match) {
				return match.getSource();
			}
		};

		// to align the table title to center
		sourceColumn
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		// Make the name column sortable.
		sourceColumn.setSortable(true);

		/*
		 * Relation cell column.$
		 */
		
		RelationCell relationCell = new RelationCell();
		Column<MatchLog,String> relationColumn = new Column<MatchLog,String>(relationCell) {
			@Override
			public String getValue(MatchLog match) {
				return match.getRelation();
			}
			
		};
		relationColumn
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		// Make the name column sortable.
		relationColumn.setSortable(true);
		
		
		/*
		 * Target cell column.$
		 */
		TextColumn<MatchLog> targetColumn = new TextColumn<MatchLog>() {
			@Override
			public String getValue(MatchLog match) {
				return match.getTarget();
			}
		};
		targetColumn
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
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
	
	@UiHandler("createbtn")
	void onCreateBtnClick(ClickEvent event){
		/*
		 * Validate the source and target text area for valid data.$
		 */
		String ext_treeSourceData = textTreeSource.getText();
		if (!validateText(ext_treeSourceData)) {
			return;
		}
		
		String ext_treeTargetData = textTreeTarget.getText();
		if (!validateText(ext_treeTargetData)) {
			return;
		}
		
		/*
		 * Fetch tree models for both source and target.$ 
		 */
		CustomTreeModel modelSourceTree = (CustomTreeModel) this.cellTreeSource
				.getTreeViewModel();
		CustomTreeModel modelTargetTree = (CustomTreeModel) this.cellTreeTarget
				.getTreeViewModel();
		
		/*
		 * Match the two models
		 */
		ArrayList<MatchLog> matchedModels = matchModels(modelSourceTree.getAllNodes(),modelTargetTree.getAllNodes());
		
		
		/*
		 * Display the related models in the table.
		 */
		cellTable.setRowData(matchedModels);
		
		
		
	}
	
	/*
	 * Match elements of two lists. $
	 */
	private ArrayList<MatchLog> matchModels(ArrayList<TNode> sourceNodes,
			ArrayList<TNode> targetNodes) {
		
		ArrayList<MatchLog> relatedNodes = new ArrayList<MatchLog>();
		/*
		 * Iterate over the source tree.$
		 */
		for(int i=0; i<sourceNodes.size();i++){
			
			/*
			 * Pick a source node to match.$
			 */
			TNode currSourceNode = (TNode)(sourceNodes.get(i));
			/*
			 * Iterate over the target tree.$
			 */
			System.out.println("currSource:"+currSourceNode.getNodeLabel());
			for(int j=0;j<targetNodes.size();j++){
				/*
				 * Pick a target node and match the source node with it.$
				 */
				TNode currTargetNode = (TNode)targetNodes.get(j);
				
				/*
				 * Apply str comparison on the labels of the two nodes.
				 */
				System.out.println("currTarget:"+currTargetNode.getNodeLabel());
				if(currSourceNode.getNodeLabel().equalsIgnoreCase(currTargetNode.getNodeLabel())){
					relatedNodes.add(new MatchLog(currSourceNode.getNodeLabel(),"Equivalence",currTargetNode.getNodeLabel()));
					System.out.println(currSourceNode.getNodeLabel()+"=="+currTargetNode.getNodeLabel());
				}
			}
			
			
		}
		return relatedNodes;
	}

	@UiHandler("saveSourceTreeText")
	void onSaveSourceTreeTextClick(ClickEvent event) {
		this.saveSourceTreeText.setText("Syncing...");
		this.saveSourceTreeText.setEnabled(false);
		refreshSourceTree();
		this.saveSourceTreeText.setText("Sync");
		this.saveSourceTreeText.setEnabled(true);
		this.treeSourceOptionTabs.selectTab(0, true);

	}

	@UiHandler("saveTargetTreeText")
	void onSaveTargetTreeTextClick(ClickEvent event) {

		this.saveTargetTreeText.setText("Syncing...");
		this.saveTargetTreeText.setEnabled(false);
		refreshTargetTree();
		this.saveTargetTreeText.setText("Sync");
		this.saveTargetTreeText.setEnabled(true);
		this.treeTargetOptionTabs.selectTab(0, true);

	}

	void refreshSourceText() {
		this.textTreeSource.setVisible(false);
		// System.out.println("text tree is hiddden");
		CustomTreeModel model = (CustomTreeModel) this.cellTreeSource
				.getTreeViewModel();

		this.textTreeSource.setText(model.getAllNodeStrings());

		this.textTreeSource.setVisible(true);
		// System.out.println("text tree is visible");
	}

	void refreshSourceTree() {
		// this.cellTreeSource.setVisible(false);

		String ext_treeData = textTreeSource.getText();
		if (!validateText(ext_treeData)) {
			return;
		}
		System.out.println("from text=" + ext_treeData);
		ext_treeData = ext_treeData.trim();
		String[] treeNodes = ext_treeData.split(";");
		// Create a model for the tree.
		CustomTreeModel model = (CustomTreeModel) this.cellTreeSource
				.getTreeViewModel();
		model.clearAll(null);
		for (int i = 0; i < treeNodes.length; i++) {
			System.out.println("node=" + treeNodes[i]);
			if (treeNodes[i] != "") {
				String[] nodedata = treeNodes[i].trim().split(",");
				if (nodedata.length < 2) {
					System.out.println("Skipping line:" + treeNodes[i] + ".");
					continue;
				}

				model.addNode(nodedata[1], nodedata[0]);

			}

		}

		// this.cellTreeSource = new CellTree(model,null);
		model.refresh();
		// System.out.println("Closing Tree Nodes");
		// closeNodes(this.cellTreeSource.getRootTreeNode());

		System.out.println("Tree Ready");

		// this.cellTreeSource.setVisible(true);

	}

	private void closeNodes(TreeNode tn) {
		if (tn == null)
			return;
		System.out.println("children for::" + tn.getChildCount());
		for (int childInd = 0; childInd < tn.getChildCount(); childInd++) {
			TreeNode closedChildNode = tn.setChildOpen(childInd, false);
			closeNodes(closedChildNode);

		}

	}

	void refreshTargetText() {
		// this.textTreeTarget.setVisible(false);
		System.out.println("Target text tree is hiddden");
		CustomTreeModel model = (CustomTreeModel) this.cellTreeTarget
				.getTreeViewModel();

		this.textTreeTarget.setText(model.getAllNodeStrings());

		this.textTreeTarget.setVisible(true);
		System.out.println("Target text tree is visible");
	}

	void refreshTargetTree() {
		// this.cellTreeTarget.setVisible(false);

		String ext_treeData = textTreeTarget.getText();

		if (!validateText(ext_treeData)) {
			return;
		}
		ext_treeData = ext_treeData.trim();
		String[] treeNodes = ext_treeData.split(";");
		// Create a model for the tree.
		CustomTreeModel model = (CustomTreeModel) this.cellTreeTarget
				.getTreeViewModel();
		model.clearAll(null);
		for (int i = 0; i < treeNodes.length; i++) {
			if (treeNodes[i] != "") {
				String[] nodedata = treeNodes[i].trim().split(",");
				if (nodedata.length < 2) {
					System.out.println("Skipping line:" + treeNodes[i] + ".");
					continue;
				}

				model.addNode(nodedata[1], nodedata[0]);

			}

		}
		System.out.println("Target Tree Ready");
		model.refresh();
		// closeNodes(this.cellTreeTarget.getRootTreeNode());
		// this.cellTreeTarget.setVisible(true);

	}

	@UiHandler("createSource")
	void onCreateSourceClick(ClickEvent event) {
		CustomTreeModel model = (CustomTreeModel) this.cellTreeSource
				.getTreeViewModel();
		model.clearAll(null);
		model.createDefault(null);
		model.refresh();
		closeNodes(this.cellTreeSource.getRootTreeNode());

	}

	@UiHandler("createTarget")
	void onCreateTargetClick(ClickEvent event) {
		CustomTreeModel model = (CustomTreeModel) this.cellTreeTarget
				.getTreeViewModel();
		model.clearAll(null);
		model.createDefault(null);
		model.refresh();
		closeNodes(this.cellTreeTarget.getRootTreeNode());

	}

	void showError(String errorStr) {
		msg = new MsgPopUp(errorStr);
		msg.show();
	}

	@UiHandler("treeTargetOptionTabs")
	void onTreeTargetOptionTabsSelection(SelectionEvent<Integer> event) {
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
			// refreshTargetTree();

		}

	}

	@UiHandler("treeSourceOptionTabs")
	void onTreeSourceOptionTabsSelection(SelectionEvent<Integer> event) {
		// Tree area for Source selected
		if (event.getSelectedItem() == 0) {
			// System.out.println("0 selected");
			addSourceNode.setEnabled(true);
			addSourceChildNode.setEnabled(true);
			deleteSourceNode.setEnabled(true);
			// refreshSourceTree();
		}
		// Text area for source is selected
		if (event.getSelectedItem() == 1) {
			// System.out.println("1 selected");
			addSourceNode.setEnabled(false);
			addSourceChildNode.setEnabled(false);
			deleteSourceNode.setEnabled(false);
			refreshSourceText();
		}
	}

	@UiHandler("addSourceChildNode")
	void onAddSourceChildNodeClick(ClickEvent event) {

		CustomTreeModel model = (CustomTreeModel) this.cellTreeSource
				.getTreeViewModel();
		TNode selNode = model.getSelectionModel().getSelectedObject();
		if (selNode == null) {
			showError("No node selected");
			return;
		}
		// TNode parentNode = selNode.getParent();
		System.out.println("selected node=" + selNode);
		if (selNode != null) {
			model.createDefault(selNode);
			// selNode.addChild(null);

		}
		model.refresh();
	}

	@UiHandler("deleteSourceNode")
	void onDeleteSourceNodeClick(ClickEvent event) {
		CustomTreeModel model = (CustomTreeModel) this.cellTreeSource
				.getTreeViewModel();
		TNode selNode = model.getSelectionModel().getSelectedObject();

		if (selNode != null) {
			System.out
					.println("clearing children of " + selNode.getNodeLabel());
			if (selNode.getParent() != null) {
				selNode.clearAll();
				System.out.println("cleared, Now checking the parent("
						+ selNode.getParent() + ")");
				//
				// if((model.getNodeAt(model.getRoot(),null)).getNodeLabel().equalsIgnoreCase(selNode.getParent().getNodeLabel())){
				// System.out.println("Removing Node from under root");
				// model.removeNode(selNode);
				// }else{
				selNode.getParent().removeChild(selNode);
				// }
				System.out.println(selNode.getNodeLabel()
						+ " Removed from tree.");
			} else {
				showError("Cannot delete Root.");
			}
		}
		// System.out.println("----------------------");
		// model.getSelectionModel().clear();
		model.refresh();
	}

	@UiHandler("addTargetChildNode")
	void onAddTargetChildNodeClick(ClickEvent event) {

		CustomTreeModel model = (CustomTreeModel) this.cellTreeTarget
				.getTreeViewModel();
		TNode selNode = model.getSelectionModel().getSelectedObject();
		if (selNode == null) {
			showError("No node selected");
			return;
		}
		if (selNode != null) {

			model.createDefault(selNode);

		}
		model.refresh();
	}

	@UiHandler("deleteTargetNode")
	void onDeleteTargetNodeClick(ClickEvent event) {
		CustomTreeModel model = (CustomTreeModel) this.cellTreeTarget
				.getTreeViewModel();
		TNode selNode = model.getSelectionModel().getSelectedObject();
		System.out.println("selected Node=" + selNode.toString());
		if (selNode != null) {
			selNode.clearAll();
			if (selNode.getParent() != null) {
				selNode.getParent().removeChild(selNode);
			} else {
				showError("Cannot delete Root.");
			}
		}
		// model.getSelectionModel().clear();
		model.refresh();
	}

	@UiHandler("addSourceNode")
	void onAddSourceNodeClick(ClickEvent event) {
		CustomTreeModel model = (CustomTreeModel) this.cellTreeSource
				.getTreeViewModel();
		TNode selNode = model.getSelectionModel().getSelectedObject();
		if (selNode != null) {
			TNode parentNode = selNode.getParent();
			if (parentNode == null) {
				showError("Cannot add a sibling to the root.");

			} else {

				model.createDefault(parentNode);
			}
		}
		model.refresh();

	}

	private boolean validateText(String ext_treeData) {
		ext_treeData = ext_treeData.trim();
		if (ext_treeData.equalsIgnoreCase("")) {
			showError("The Textual Tree Cannot be Empty.");
			return false;
		} else {
			String[] treeNodes = ext_treeData.split(";");
			if (treeNodes.length < 1) {
				showError("No Nodes Exist.");
				return false;
			} else {
				String[] nodes = new String[treeNodes.length];

				for (int i = 0; i < treeNodes.length; i++) {

					if (treeNodes[i].equalsIgnoreCase("")) {
						showError("The Node in Line" + i + "is Empty");
						return false;
					} else {
						String[] tNodes = treeNodes[i].split(",");
						if (tNodes.length != 2) {

							showError("The Tree is not Correctly Entered.");
							return false;
						} else {

							for (int j = 0; j < nodes.length; j++) {

								if (tNodes[0].equalsIgnoreCase(nodes[j])) {
									showError("The Node Already Exist in The Tree.");
									return false;

								}
							}
							nodes[i] = tNodes[0];
							tNodes[0] = tNodes[0].trim();
							tNodes[1] = tNodes[1].trim();
						
							if (tNodes[1].equalsIgnoreCase("-")) {
								//showError("root."+tNodes[1] +" "+ tNodes[0]);
							} else if (tNodes[1].equalsIgnoreCase(tNodes[0])) {

								showError("The Parent Node Cannot Have The Same Name as Child Node.");
								return false;
							} else {
								boolean parentInNodes = false;
								for (int j = 0; j < nodes.length; j++) {

									if (tNodes[1].equalsIgnoreCase(nodes[j])) {

										parentInNodes = true;
									}

								}

								if (!parentInNodes) {
									showError("The Parent Node is not Matching.");
									return false;

								}

							}
						}
					}

				}
			}
		}
		return true;

	}

	@UiHandler("addTargetNode")
	void onAddTargetNodeClick(ClickEvent event) {
		CustomTreeModel model = (CustomTreeModel) this.cellTreeTarget
				.getTreeViewModel();
		TNode selNode = model.getSelectionModel().getSelectedObject();

		if (selNode != null) {
			TNode parentNode = selNode.getParent();
			if (parentNode == null) {
				showError("Cannot add a sibling to the root.");
			} else {
				model.createDefault(parentNode);
			}
		}
		model.refresh();
	}

}
