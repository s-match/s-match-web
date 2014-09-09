package main.java.it.unitn.disi.smatch.web.client.model;

import java.util.ArrayList;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.TreeViewModel;

//The model that defines the nodes in the tree.
public class CustomTreeModel implements TreeViewModel {
	ListDataProvider<TNode> rootDataProvider = null;
	// private TNode root = null;
	private ValueUpdater<TNode> valueUpdater;
	private SingleSelectionModel<TNode> selectionModel;
	

	public CustomTreeModel() {
		this.rootDataProvider = new ListDataProvider<TNode>(
				new ArrayList<TNode>());
		selectionModel = new SingleSelectionModel<TNode>();
		selectionModel.addSelectionChangeHandler(new Handler(){

			public void onSelectionChange(SelectionChangeEvent event) {
				TNode selNode = selectionModel.getSelectedObject();
				if(selNode!=null && selNode.getParent()==null){
					System.out.println("ROOT SELECTED");
				}
				
			}
			
		});
		valueUpdater = new ValueUpdater<TNode>() {
			public void update(TNode changedNode) {
				System.out.println("new value:" + changedNode.getNodeLabel());
			}
		};
		this.createDefault(null);
		// this.rootDataProvider.refresh();
	}
	
	public ValueUpdater<TNode> getValueUpdater() {
		return valueUpdater;
	}

	public void setValueUpdater(ValueUpdater<TNode> valueUpdater) {
		this.valueUpdater = valueUpdater;
	}

	public SingleSelectionModel<TNode> getSelectionModel() {
		return selectionModel;
	}

	public void setSelectionModel(SingleSelectionModel<TNode> selectionModel) {
		this.selectionModel = selectionModel;
	}

	public void addNode(String parentLabel, String label) {

		TNode parent = findNodeByLabel(parentLabel, null);
		
		// Check if already exists
		TNode existingNode = findNodeByLabel(label, parent);
		if (existingNode != null) {
			System.out.println("Node(" + label + ") already present");
			return;
		}
		//System.out.println("node(" + label + ") does not exist.");

		// Get Parent Node Id
		//System.out.println("searching for node labelled:" + parentLabel);
		if (parentLabel.equalsIgnoreCase("-")) {
			int rootId = getRoot();
//			System.out.println("rootid=" + rootId);
			if(rootId>-1){
//				System.out.println("setting label for Root("
//					+ ((TNode) rootDataProvider.getList().get(rootId))
//							.getNodeLabel() + ") as:" + label);
//			
			
				((TNode) getNodeAt(rootId,null)).setNodeLabel(label);
			
//				System.out.println("New label for Root:"
//					+ ((TNode) rootDataProvider.getList().get(rootId))
//							.getNodeLabel() + "");
			rootDataProvider.refresh();

			return;
			}else{
				TNode root = new TNode(label, TNode.TN_ROOT, null);
				this.rootDataProvider.getList().add(root);
				return;
			}
		}
		
		if (parent == null) {
			System.out.println("Parent not found for="+parentLabel);
			return;
		}

//		System.out.println("Creating new node object.");
		TNode tnode = new TNode(label, parent);

		// if(getSize()>0 &&
		// isLeaf(this.rootDataProvider.getList().get(iparentId))){
		// if(iparentId==0){
		// this.rootDataProvider.getList().get(iparentId).setLevel(TNode.TN_ROOT);
		// System.out.println(iparentId+" set as Root");
		// }else{
		// this.rootDataProvider.getList().get(iparentId).setLevel(TNode.TN_NODE);
		// System.out.println(iparentId+" set as Node");
		// }
		// }

		System.out.println(tnode.getNodeLabel()
				+ " adding in"
				+ ((parent == null) ? " root " : " parent:"
						+ parent.getNodeLabel()));
		
			parent.addChild(tnode);
		
		System.out.println("nodes in parent="+parent.getChildrenSize());



	}

	public void refresh() {
		this.rootDataProvider.flush();
		this.rootDataProvider.refresh();

	}

	
	public int getNodeInd(TNode node) {
		
		if(node.getParent()==null){
			for (int i = 0; i < getSize(); i++) {
				//System.out.println("checking="+((TNode) rootDataProvider.getList().get(i)).getNodeLabel()+" == "+label);
				if (((TNode) rootDataProvider.getList().get(i)).getNodeLabel()
						.equalsIgnoreCase(node.getNodeLabel())) {
					return i;
				}
			}
			return -1;
		}else{
			return node.getParent().getChildInd(node);
		}
		
	}
	
	public void removeNode(TNode selNode) {
		
		if(selNode.getParent()==null){
			int selNodeInd = this.getNodeInd(selNode);
			if(selNodeInd>0){
				this.rootDataProvider.getList().remove(selNodeInd);
			}else{
				System.out.println("Cannot find the node("+selNode.getNodeLabel()+") under root with index("+selNodeInd+")");
			}
		}else{
			selNode.getParent().removeChild(selNode);
		}
		
		
		
	}
	

	public TNode getNodeAt(int i, TNode parent) {
		if (parent == null) {
			if (i < 0 || i >= getSize()) i = 0;
			return this.rootDataProvider.getList().get(i);
		} else {
			System.out.println("index here is="+i);
			return parent.getChildAt(i);
		}
	}

	public String getAllNodeStrings() {
		ArrayList<TNode> nodelist = new ArrayList<TNode>();
		String retNodeValues = "";
		for (int i = 0; i < rootDataProvider.getList().size(); i++) {
			TNode retNode = getNodeAt(i, null);
			nodelist.add(retNode);
			retNodeValues += retNode.toString();
		}

		for (int i = 0; i < nodelist.size(); i++) {
			TNode currNode = nodelist.get(i);
			System.out.println("checking children("+currNode.getChildrenSize()+")");
			for (int j = 0; j < currNode.getChildrenSize(); j++) {
				System.out.println("at index "+j+" of "+currNode);
				TNode retNode = getNodeAt(j, currNode);
				System.out.println(retNode);
				nodelist.add(retNode);
				retNodeValues += retNode.toString();
			}
		}
		System.out.println("ret String="+retNodeValues);
		return retNodeValues;
	}

	public TNode findNodeByLabel(String nodeLabel, TNode parent) {
		nodeLabel = nodeLabel.trim();
		if ("-".equalsIgnoreCase(nodeLabel)) {
			
//			System.out.println("label is -, returning Root node index.");
			int rootId = getRoot();
			if(rootId<0){
				return null;
			}else{
				TNode root = (TNode) this.getNodeAt(rootId,null);
				return root;
			}
		}

		if (parent == null) {
			for (int i = 0; i < this.rootDataProvider.getList().size(); i++) {
				TNode currNode = (TNode) rootDataProvider.getList().get(i);
				if (currNode.getNodeLabel().equalsIgnoreCase(nodeLabel)) {
//					System.out.println("" + nodeLabel + "=="
//							+ currNode.getNodeLabel() + "");
					return currNode;
				}
				// recursive checking, to make sure the child is also not
				// present in the Node's children.
//				System.out.println("Search for(" + nodeLabel + ") under "
//						+ currNode.getNodeLabel() + "");
				TNode foundNode = findNodeByLabel(nodeLabel, currNode);
				if (foundNode != null) {
//					System.out.println("Returning:" + foundNode.getNodeLabel());
					return foundNode;
				}
			}

		} else {
//			System.out.print("Searching for child label:" + nodeLabel + ":::");
			TNode foundNode = parent.getChild(nodeLabel);
//			System.out.println((foundNode != null) ? " Success at "
//					+ foundNode.getNodeLabel() : " Not Found ");
			return foundNode;
		}
		return null;
	}

	public int getRoot() {
		for (int i = 0; i < this.rootDataProvider.getList().size(); i++) {
			if (((TNode) rootDataProvider.getList().get(i)).isRoot()) {
				return i;
			}
		}
		return -1;
	}

	// Get the NodeInfo that provides the children of the specified value.
	public <T> NodeInfo<?> getNodeInfo(T value) {
		Cell<TNode> cell = new EditTNode(40);
		if (value == null) {
			//System.out.println("RDP size="+this.getSize());
			return new TreeViewModel.DefaultNodeInfo<TNode>(rootDataProvider,
					cell, selectionModel, valueUpdater);
		}else {
		
			//System.out.println("get node info of:"+ ((TNode) value).getNodeLabel());
			
			// Return a node info that pairs the data with a cell.
			return new TreeViewModel.DefaultNodeInfo<TNode>(
					((TNode) value).getChildrenProvider(), cell,
					selectionModel, valueUpdater);
		}

	}

	
	public boolean isLeaf(Object value) {
		if (value instanceof TNode) {
			TNode lNode = (TNode) value;
			// TNode lNode = getNode((String)value);

			return (lNode).isLeaf();
		}
		return false;
	}

	public int getSize() {
		return this.rootDataProvider.getList().size();
	}

	public ArrayList<TNode> getAllNodes() {
		ArrayList<TNode> nodelist = new ArrayList<TNode>();
		for (int i = 0; i < rootDataProvider.getList().size(); i++) {
			TNode retNode = getNodeAt(i, null);
			nodelist.add(retNode);
		}

		for (int i = 0; i < nodelist.size(); i++) {
			TNode currNode = nodelist.get(i);
			for (int j = 0; j < currNode.getChildrenSize(); j++) {
				TNode retNode = getNodeAt(j, currNode);
				nodelist.add(retNode);
			}
		}
		return nodelist;
	}

	public void clearAll(TNode parent) {
		
		if(parent==null){
			//System.out.println("Clearing Root");
			this.rootDataProvider.getList().clear(); 
		}else{
			//System.out.println("Removing all children from Parent("+parent.getNodeLabel()+")");
			parent.getChildrenProvider().getList().clear();
		}

	}

	public void createDefault(TNode parent) {
		if(parent==null){
			TNode root = new TNode("Root", TNode.TN_ROOT, null);
			//root.setChildrenProvider(rootDataProvider);
			this.rootDataProvider.getList().add(root);
			//this.createDefault(root);
		}else{
			System.out.println("number of children under this node="+parent.getChildrenSize());
			TNode node = new TNode(parent.genNodeName(), TNode.TN_LEAF, parent);
			System.out.println("Adding new node="+node);
			parent.addChild(node);
		}
		
		
	}

}
