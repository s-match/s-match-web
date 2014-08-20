package main.java.it.unitn.disi.smatch.web.client.model;

import com.google.gwt.view.client.ListDataProvider;

public class TNode{
	private String nodeLabel = "";
	public final static int TN_ROOT = 0;
	public final static int TN_NODE = 1;
	public final static int TN_LEAF = 2;
	private int level = 0;
	private TNode parent = null;
	private ListDataProvider<TNode> childrenProvider = null;
	private int idIte = 0;
	
	public void refresh(){
		childrenProvider.refresh();
	}
	
	public String genNodeName(){
		idIte++;
		return "New Node"+"("+idIte+")";
	}
	public void removeChildAt(int childIndex){
		if(!hasChildren()) return;
		if(childIndex>=getChildrenSize() || childIndex<1) childIndex = 0;
		
		childrenProvider.getList().remove(childIndex);
		refresh();
	}
	
	public int addChild(TNode child){
		if(child==null){
			//System.out.println(this.getParent());
			if(this.getParent() == null || this.getParent().getNodeLabel() == "-" ){
				System.out.println("Incorrect way to add a child node to root. Use rootDataProvider instead.");
			}else{
				child = new TNode("ChildNode"+getChildrenSize(), TNode.TN_LEAF, this);
			}
		}
		
		if(this.level==TN_LEAF) this.level = TN_NODE;
		
		boolean isAddSuccess = childrenProvider.getList().add(child);
		refresh();
		if(isAddSuccess){
			return getChildrenSize();
		}
		return -1;
	}
	
	public TNode getChild(String childLabel){
		if(!hasChildren()) return null;
		for(int i=0; i< getChildrenSize();i++){
			if(childLabel.equalsIgnoreCase(getChildAt(i).nodeLabel))
			return getChildAt(i);
		}
		return null;
		
	}
	
	public String getNodeLabel() {
		return nodeLabel;
	}

	public void setNodeLabel(String nodeLabel) {
		this.nodeLabel = nodeLabel;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public TNode getParent() {
		return parent;
	}

	public void setParent(TNode parent) {
		this.parent = parent;
	}

	public ListDataProvider<TNode> getChildrenProvider() {
		return childrenProvider;
	}

	public void setChildrenProvider(ListDataProvider<TNode> childrenProvider) {
		this.childrenProvider = childrenProvider;
	}

	public TNode getChildAt(int childIndex){
		if(!hasChildren()) return null;
		
		if(childIndex>=getChildrenSize() || childIndex<1) childIndex = 0;
		System.out.println("Index here is ="+childIndex);
		TNode child = childrenProvider.getList().get(childIndex);
		refresh();
		return child;
		
	}
	public boolean isLeaf(){
		if(level==TN_LEAF){
			if(getChildrenSize()>0){
				level = TN_NODE;
				return false;
			}else{
				return true;
			}
		}else{
			if(this.parent==null || this.parent.nodeLabel.equalsIgnoreCase("-")){
				return false;
			}
			if(!hasChildren()){
				level = TN_LEAF;
				return true;
			}else{
				return false;
			}
		}
	}
	public boolean isRoot(){
		System.out.println("I am Root"+this.nodeLabel+"||level="+level);
		if(level==TN_ROOT){
				return true;
		}
		return false;
		
	}
	public int getChildrenSize(){
		return childrenProvider.getList().size();
	}
	public boolean hasChildren(){
		if(getChildrenSize()>0){
			return true;
		}
		return false;
	}
	public TNode(String nodeLabel, int level,TNode parent) {
		this.nodeLabel = nodeLabel;
		this.level = level;
		this.parent = parent;
		childrenProvider = new ListDataProvider<TNode>();
	}
	public TNode(String nodeLabel,TNode parent) {
		this.nodeLabel = nodeLabel;
		if(parent==null){
			this.level = TN_ROOT;
		}else{
			System.out.println("I am a leaf now="+nodeLabel);
			this.level = TN_LEAF;
		}
		this.parent = parent;
		childrenProvider = new ListDataProvider<TNode>();
	}
	@Override
	public String toString(){
		String retStr = this.nodeLabel+","+((this.parent==null)?"-":this.parent.nodeLabel)+";\r\n";
		//System.out.println("Node as String:"+retStr);
		return retStr;
	}

	public void clearAll() {
		this.childrenProvider = new ListDataProvider<TNode>();
		this.childrenProvider.refresh();
		
	}
	public void removeChild(TNode child){
		System.out.println(child+", hasChildren= "+hasChildren()+", size:"+getChildrenSize());
		if(!hasChildren()) return;
		for(int i=0; i< getChildrenSize();i++){
			if(child.getNodeLabel().equalsIgnoreCase(getChildAt(i).nodeLabel))
			this.childrenProvider.getList().remove(i);
		}
	}

	public int getChildInd(TNode node) {
		if(!hasChildren()) return -1;
		for(int i=0; i< getChildrenSize();i++){
			if(node.getNodeLabel().equalsIgnoreCase(getChildAt(i).nodeLabel))
			return i;
		}
		return -1;
	}
}
