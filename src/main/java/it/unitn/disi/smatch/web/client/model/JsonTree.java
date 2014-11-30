package it.unitn.disi.smatch.web.client.model;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;


public class JsonTree {

    public JsonTree() {

    }

    public void addNodesToTree(JSONArray childArr, TNode parent, CustomTreeModel model) {

        if (childArr != null) {
            JSONObject objItem;
            for (int i = 0, len = childArr.size(); i < len; i++) {
                objItem = childArr.get(i).isObject();
                if (objItem != null) {
                    JSONString nodeLabel = objItem.get("name").isString();
                    JSONArray nodeChildren = objItem.get("children").isArray(); //$NON-NLS-1$
                    if (nodeLabel == null) {
                        System.out.println("No Node found");
                        return;
                    }
                    if (nodeChildren == null) {
                        System.out.println("No children found");
                        return;
                    }
                    System.out.println(parent.getid() + " :: " + nodeLabel.stringValue());
                    TNode savedNode = model.addNode(parent.getid(), nodeLabel.stringValue());
                    if (savedNode == null) {
                        System.out.println("There was a problem adding the nodes");
                        return;
                    }

                    addNodesToTree(nodeChildren, savedNode, model);
                }
            }
        }


    }

    public void readJson(String treedata, CustomTreeModel model) {
        try {

            JSONObject objTree = JSONParser.parseStrict(treedata).isObject();
//System.out.println(objTree);
            if (objTree != null) {
                JSONString nodeLabel = objTree.get("name").isString();
                JSONArray nodeChildren = objTree.get("children").isArray(); //$NON-NLS-1$
                if (nodeLabel == null) {
                    System.out.println("No Root found");
                    return;
                }
                TNode savedNode = model.addNode(null, nodeLabel.stringValue());
                if (savedNode == null) {
                    System.out.println("There was a problem adding the nodes");
                    return;
                }
                System.out.println("node saved:" + savedNode.getNodeLabel());
                addNodesToTree(nodeChildren, savedNode, model);
            }

        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }


    }

    public void populateChildList(JSONArray list, TNode parent) {


        //System.out.println("children:"+parent.getChildrenSize());
        for (int i = 0; i < parent.getChildrenSize(); i++) {
            JSONObject obj = new JSONObject();

            TNode currNode = parent.getChildAt(i);
            JSONString nodelabel = new JSONString(currNode.getNodeLabel());
            obj.put("name", nodelabel);
            JSONArray childList = new JSONArray();
            populateChildList(childList, currNode);
            obj.put("children", childList);
            list.set(list.size(), obj);
        }


    }

    public String writeJson(CustomTreeModel model) {

        JSONObject obj = new JSONObject();

        for (int i = 0; i < model.rootDataProvider.getList().size(); i++) {
            TNode currNode = model.getNodeAt(i, null);
            //System.out.println(currNode.getNodeLabel());
            obj.put("name", new JSONString(currNode.getNodeLabel()));
            JSONArray list = new JSONArray();
            populateChildList(list, currNode);
            obj.put("children", list);


        }

        String formatedJson = formatJsonString(obj);


        return formatedJson;

    }

    public String formatJsonArr(JSONArray children, int tabIndex) {
        if (children == null) {
            return "[],\n";
        } else {
            String formattedChild = "";
            for (int j = 0; j < tabIndex; j++) {
                formattedChild += "\t";
            }
            formattedChild += "[\n";
            for (int i = 0; i < children.size(); i++) {

                JSONObject currJsonObj = (JSONObject) children.get(i);

                JSONString nodeLabel = currJsonObj.get("name").isString();
                JSONArray nodeChildren = currJsonObj.get("children").isArray(); //$NON-NLS-1$
                if (nodeLabel == null) {
                    return "";
                } else {
                    for (int j = 0; j < tabIndex; j++) {
                        formattedChild += "\t";
                    }
                    formattedChild += "{\n";


                    for (int j = 0; j < tabIndex; j++) {
                        formattedChild += "\t";
                    }
                    formattedChild += "\"name\":" + currJsonObj.get("name").toString() + ",\n";
                    if (nodeChildren != null && nodeChildren.size() > 0) {
                        for (int j = 0; j < tabIndex; j++) {
                            formattedChild += "\t";
                        }
                        formattedChild += "\"children\":\n" + formatJsonArr(nodeChildren, tabIndex + 1) + ",\n";
                    } else {
                        for (int j = 0; j < tabIndex; j++) {
                            formattedChild += "\t";
                        }
                        formattedChild += "\"children\":[]\n";
                    }
                    if (formattedChild.endsWith(",\n")) {
                        formattedChild = formattedChild.substring(0, formattedChild.lastIndexOf(","));
                    }
                    for (int j = 0; j < tabIndex; j++) {
                        formattedChild += "\t";
                    }
                    formattedChild += "},\n";


                }

            }
            System.out.println("before removal:" + formattedChild);

            if (formattedChild.endsWith(",\n")) {
                formattedChild = formattedChild.substring(0, formattedChild.lastIndexOf(","));
            }
            System.out.println("after removal:" + formattedChild);
            for (int j = 0; j < tabIndex; j++) {
                formattedChild += "\t";
            }
            formattedChild += "]\n";

            return formattedChild;
        }


    }

    public String formatJsonString(JSONObject jsonObj) {
        String formattedStr = "{\n";
        if (jsonObj != null) {

            JSONString nodeLabel = jsonObj.get("name").isString();
            JSONArray nodeChildren = jsonObj.get("children").isArray(); //$NON-NLS-1$
            if (nodeLabel == null) {
                System.out.println("No Root found");
            } else {
                formattedStr += "\"name\":" + jsonObj.get("name").toString() + ",\n";
                if (nodeChildren != null && nodeChildren.size() > 0) {
                    formattedStr += "\"children\":" + formatJsonArr(nodeChildren, 1) + ",\n";
                } else {
                    formattedStr += "\"children\":[]\n";
                }
            }
        }
        //System.out.println(formattedStr);
        if (formattedStr.endsWith(",\n")) {
            formattedStr = formattedStr.substring(0, formattedStr.lastIndexOf(","));
        }
        formattedStr += "}\n";
        return formattedStr;

    }

    public void readAsterText(String ext_treeData, CustomTreeModel model) {
        // TODO Auto-generated method stub

    }
}
