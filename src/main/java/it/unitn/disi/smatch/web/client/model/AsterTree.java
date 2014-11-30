package it.unitn.disi.smatch.web.client.model;

import java.util.ArrayList;


public class AsterTree {
    public String lineSeparator = "\n";
    public String listLevelIdenRegex = "\\*\\*";
    public String listLevelIden = "**";

    public AsterTree() {

    }

    public int countListLevels(String line) {
        int levelCount = 0;
        int lastIndex = 0;
        //Extra check to omit any indent Characters not in the beginning of the string.
        String lineIndents = line.substring(lastIndex, line.indexOf(this.cleanLabel(line)));
        while (lastIndex != -1 && lineIndents.length() > 0) {
            lastIndex = lineIndents.indexOf(listLevelIden, lastIndex);
            if (lastIndex != -1) {
                levelCount++;
                lastIndex += listLevelIden.length();
            }
        }
        return levelCount;
    }

    //Remove the indent characters using regex
    public String cleanLabel(String rawName) {
        return rawName.replaceAll(listLevelIdenRegex, "");
    }

    public void read(String ext_treeData, CustomTreeModel model) {

        String rawlines[] = ext_treeData.split(lineSeparator);

        if (rawlines.length < 1) {
            System.out.println("Not enough lines in text to generate a tree");
            return;
        } else {
            int lineIte = 0;

            // Calculating Root Node

            boolean customRoot = false; //Should we add a root or is there one already in the tree?
            int minLevel = this.countListLevels(rawlines[lineIte]);
            for (int i = 1; i < rawlines.length; i++) {
                if (minLevel == this.countListLevels(rawlines[i])) {
                    customRoot = true;
                    break;
                }
                if (minLevel > this.countListLevels(rawlines[i])) {
                    System.out.println("The first line must have the shortest indentation(usually no indent for Root).");
                    System.out.println(minLevel + "<<<" + this.countListLevels(rawlines[i]));
                    return;
                }
            }

            if (customRoot) {
                //Creating a default Root
                model.createDefault(null);
                lineIte = 0;
            } else {
                model.addNode(null, this.cleanLabel(rawlines[lineIte]));
                minLevel++;
                rawlines[lineIte++] = "";
            }
            //Now we will have a root node
            TNode parentNode = model.getNodeAt(0, null);
            addNodesToTree(rawlines, minLevel, parentNode, model);
        }
    }

    public boolean skipLine(String line) {
        if (line.equals("") || line.startsWith("#")) {
            return true;
        }
        return false;
    }

    public void addNodesToTree(String[] rawlines, int curr_depth, TNode parentNode, CustomTreeModel model) {


        int lineIte = 0;
        System.out.println("number of lines:" + rawlines.length);
        while (lineIte < rawlines.length) {
            ArrayList<String> childLines = new ArrayList<String>();

            String line = rawlines[lineIte];
            if (this.skipLine(line)) {
                lineIte++;
                continue;
            }
            int parentIndent = this.countListLevels(line);
            // Check if the current node has more than curr_depth list indents
            if (parentIndent >= curr_depth) {

                //recursive call with data build during previous loop cycle
                TNode savedNode = model.addNode(parentNode.getid(), this.cleanLabel(line));
                lineIte++;
                while (lineIte < rawlines.length) {
                    line = rawlines[lineIte];
                    if (!this.skipLine(line)) {
                        if (this.countListLevels(line) > parentIndent) {
                            //build the childLines sub-tree array until a sibling is found
                            childLines.add(line);
                            //lineIte++;
                        } else {
                            if (childLines.size() > 0) {

                                addNodesToTree((String[]) childLines.toArray(new String[childLines.size()]), parentIndent + 1, savedNode, model);


                            } else {
                                //System.out.println("No children");
                            }
                            //All nodes should be saved by this point
                            childLines.clear();
                            //lineIte--;
                            break;
                        }
                    }
                    lineIte++;
                }
                //Clearing out any dangling nodes, because end of text was reached
                if (childLines.size() > 0) {
                    addNodesToTree((String[]) childLines.toArray(new String[childLines.size()]), parentIndent + 1, savedNode, model);
                }

            } else {
                //This node has been processed, so go back to the parent.
                return;
            }

        }


    }

    public String populateChildList(TNode parent, int level) {

        String formatedAsterText = "";
        for (int i = 0; i < parent.getChildrenSize(); i++) {

            TNode currNode = parent.getChildAt(i);
            formatedAsterText += printAsterText(currNode.getNodeLabel(), level);
            formatedAsterText += populateChildList(currNode, level + 1);

        }

        return formatedAsterText;
    }

    public String printAsterText(String label, int level) {
        for (int i = 0; i < level; i++) {
            label = this.listLevelIden + label;
        }
        return label + this.lineSeparator;
    }

    public String write(CustomTreeModel model) {
        String formatedAsterText = "";
        int level = 0;
        for (int i = 0; i < model.rootDataProvider.getList().size(); i++) {
            TNode currNode = model.getNodeAt(i, null);
            formatedAsterText += printAsterText(currNode.getNodeLabel(), level);
            formatedAsterText += populateChildList(currNode, level + 1);
        }
        return formatedAsterText;

    }


}
