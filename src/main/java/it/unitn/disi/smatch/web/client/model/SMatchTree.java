package it.unitn.disi.smatch.web.client.model;

import it.unitn.disi.smatch.web.shared.model.trees.BaseContext;
import it.unitn.disi.smatch.web.shared.model.trees.BaseNode;

public class SMatchTree {

    public SMatchTree() {

    }

    public void read(BaseContext smtContext, CustomTreeModel model) {
        //Loop through the nodes and add them to the Tree
        TNode rootNode = model.addNode(null, smtContext.getRoot().getName());

        for(int i=0;i<smtContext.getRoot().getChildCount();i++){
            BaseNode currSmNode = smtContext.getRoot().getChildAt(i);
            TNode currNode = model.addNode(rootNode.getid(),currSmNode.getName());
            reversePopulateChildList(currNode,currSmNode,model);
        }


    }

    public void reversePopulateChildList(TNode parent, BaseNode smNode,CustomTreeModel model) {

        for (int i = 0; i < parent.getChildrenSize(); i++) {
            BaseNode currSmNode = smNode.getChildAt(i);
            TNode currNode = model.addNode(parent.getid(),currSmNode.getName());
            reversePopulateChildList(currNode, currSmNode,model);
        }


    }

    public void populateChildList(BaseNode smNode, TNode parent) {

        for (int i = 0; i < parent.getChildrenSize(); i++) {
            TNode currTNode = parent.getChildAt(i);
            BaseNode currSmNode = smNode.createChild(currTNode.getNodeLabel());
            populateChildList(currSmNode,currTNode);
        }


    }

    public BaseContext write(CustomTreeModel model) {
        // create the top level of the task
        BaseContext smtContext = new BaseContext();

        //Loop through the nodes and add them to the task.
        TNode rootNode  = model.getNodeAt(model.getRoot(),null);
        smtContext.createRoot(rootNode.getNodeLabel());

        populateChildList(smtContext.getRoot(),rootNode);

        return smtContext;

    }

}
