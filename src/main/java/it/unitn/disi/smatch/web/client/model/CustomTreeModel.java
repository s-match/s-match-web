package it.unitn.disi.smatch.web.client.model;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.TreeViewModel;

import java.util.ArrayList;

//The model that defines the nodes in the tree.
public class CustomTreeModel implements TreeViewModel {
    ListDataProvider<TNode> rootDataProvider = null;
    private ValueUpdater<TNode> valueUpdater;
    private SingleSelectionModel<TNode> selectionModel;
    private Button addNode = null;
    private Button delNode = null;
    private Button addChildNode = null;

    public void addButtonRef(Button addNodeRef, Button delNodeRef, Button addChildNodeRef) {
        addNode = addNodeRef;
        delNode = delNodeRef;
        addChildNode = addChildNodeRef;


    }

    public CustomTreeModel() {
        this.rootDataProvider = new ListDataProvider<TNode>(
                new ArrayList<TNode>());
        selectionModel = new SingleSelectionModel<TNode>();
        selectionModel.addSelectionChangeHandler(new Handler() {

            public void onSelectionChange(SelectionChangeEvent event) {

                TNode selNode = selectionModel.getSelectedObject();
                if (selNode != null && selNode.getParent() == null) {
                    addNode.setEnabled(false);
                    delNode.setEnabled(false);
                    addChildNode.setEnabled(true);
                } else {
                    addNode.setEnabled(true);
                    delNode.setEnabled(true);
                    addChildNode.setEnabled(true);
                }

            }

        });
        valueUpdater = new ValueUpdater<TNode>() {
            public void update(TNode changedNode) {

            }
        };
        this.createDefault(null);
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

    public TNode addNode(String nodeid, String label) {

        if (nodeid == null) {
            TNode root = new TNode(label, TNode.TN_ROOT, null);
            this.rootDataProvider.getList().add(root);
            return root;
        }

        TNode parent = findNodeById(nodeid, null);

        if (parent == null) {
            System.out.println("Parent not found for id=" + nodeid);
            return null;
        }

        TNode tnode = new TNode(label, parent);

        parent.addChild(tnode);
        return tnode;

    }

    public void refresh() {
        this.rootDataProvider.flush();
        this.rootDataProvider.refresh();

    }


    public int getNodeInd(TNode node) {

        if (node.getParent() == null) {
            for (int i = 0; i < getSize(); i++) {

                if (((TNode) rootDataProvider.getList().get(i)).getNodeLabel()
                        .equalsIgnoreCase(node.getNodeLabel())) {
                    return i;
                }
            }
            return -1;
        } else {
            return node.getParent().getChildInd(node);
        }

    }

    public void removeNode(TNode selNode) {

        if (selNode.getParent() == null) {
            int selNodeInd = this.getNodeInd(selNode);
            if (selNodeInd > 0) {
                this.rootDataProvider.getList().remove(selNodeInd);
            } else {
                System.out.println("Cannot find the node(" + selNode.getNodeLabel() + ") under root with index(" + selNodeInd + ")");
            }
        } else {
            selNode.getParent().removeChild(selNode);
        }


    }


    public TNode getNodeAt(int i, TNode parent) {
        if (parent == null) {
            if (i < 0 || i >= getSize()) i = 0;
            return this.rootDataProvider.getList().get(i);
        } else {
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

            for (int j = 0; j < currNode.getChildrenSize(); j++) {
                TNode retNode = getNodeAt(j, currNode);
                nodelist.add(retNode);
                retNodeValues += retNode.toString();
            }
        }
        return retNodeValues;
    }

    public TNode findNodeById(String nodeId, TNode parent) {
        /*
		 * Search within the rootDataProvider first
		 */
        if (parent == null) {
            for (int i = 0; i < this.rootDataProvider.getList().size(); i++) {
                TNode currNode = (TNode) rootDataProvider.getList().get(i);
                if (currNode.getid().equalsIgnoreCase(nodeId)) {
                    return currNode;
                } else {
                    TNode foundNode = this.findNodeById(nodeId, currNode);
                    if (foundNode != null) {
                        return foundNode;
                    }
                }
            }
            return null;
        } else {
            TNode foundNode = parent.getChildById(nodeId);
            if (foundNode != null)
            return foundNode;
        }
        return null;

    }

    public TNode findNodeByLabel(String nodeLabel, TNode parent) {
        nodeLabel = nodeLabel.trim();
        if ("-".equalsIgnoreCase(nodeLabel)) {
            int rootId = getRoot();
            if (rootId < 0) {
                return null;
            } else {
                TNode root = (TNode) this.getNodeAt(rootId, null);
                return root;
            }
        }

        if (parent == null) {
            for (int i = 0; i < this.rootDataProvider.getList().size(); i++) {
                TNode currNode = (TNode) rootDataProvider.getList().get(i);
                if (currNode.getNodeLabel().equalsIgnoreCase(nodeLabel)) {
                    return currNode;
                }
                // recursive checking, to make sure the child is also not
                // present in the Node's children.
                TNode foundNode = findNodeByLabel(nodeLabel, currNode);
                if (foundNode != null) {
                    return foundNode;
                }
            }

        } else {
            TNode foundNode = parent.getChildByLabel(nodeLabel);
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
            return new DefaultNodeInfo<TNode>(rootDataProvider,
                    cell, selectionModel, valueUpdater);
        } else {

            // Return a node info that pairs the data with a cell.
            return new DefaultNodeInfo<TNode>(
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

        if (parent == null) {
            this.rootDataProvider.getList().clear();
        } else {
            parent.getChildrenProvider().getList().clear();
        }

    }

    public void createDefault(TNode parent) {
        if (parent == null) {
            TNode root = new TNode("Root", TNode.TN_ROOT, null);
            this.rootDataProvider.getList().add(root);
        } else {
            TNode node = new TNode(parent.genNodeName(), TNode.TN_LEAF, parent);
            parent.addChild(node);
        }


    }

}
