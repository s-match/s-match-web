package it.unitn.disi.smatch.web.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.*;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.*;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.TreeViewModel;
import it.unitn.disi.smatch.web.client.model.*;
import it.unitn.disi.smatch.web.client.widget.MappingReporter;
import it.unitn.disi.smatch.web.client.widget.MsgPopUp;
import it.unitn.disi.smatch.web.shared.model.mappings.MappingElement;
import it.unitn.disi.smatch.web.shared.model.mappings.NodesMatrixMapping;
import it.unitn.disi.smatch.web.shared.model.tasks.MatchingTask;
import it.unitn.disi.smatch.web.shared.model.trees.BaseContext;
import it.unitn.disi.smatch.web.shared.model.trees.BaseContextPair;
import it.unitn.disi.smatch.web.shared.model.trees.BaseNode;
import com.google.gwt.core.client.EntryPoint;
import com.github.nmorel.gwtjackson.client.ObjectMapper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class SMatchWeb extends Composite implements ServerTaskOpener,EntryPoint {

    public static interface BaseContextPairMapper extends ObjectMapper<BaseContextPair> {}
    public static interface MatchingTaskMapper extends ObjectMapper<MatchingTask> {}

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
    Button createbtn;
    @UiField
    TextArea textTreeSource;
    @UiField
    TextArea textTreeTarget;
    @UiField(provided = true)
    ListBox cbConfig;

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
    private Button createSource;
    private Button createTarget;
    MappingReporter serverComUI;
    boolean notSourceEmpty = false;
    boolean notTargetEmpty = false;

    public void onModuleLoad() {

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
	 * The main container
	 */
        DockLayoutPanel dlp_mainContainer = new DockLayoutPanel(Unit.EM);
        dlp_mainContainer.setHeight("800px");
        dlp_mainContainer.setWidth("100%");
		
	/*
	 * Generic mapping buttons and configurations
	 */
        SimplePanel sp_GenericOptions = new SimplePanel();
        sp_GenericOptions.setStyleName("header");
		
		/*
		 * Horizontal Panel to add items horizontally
		 */
        HorizontalPanel hp_ItemContainer = new HorizontalPanel();
			
		/*
		 * Cell for keeping Mapping button and label
		 */
        HorizontalPanel hp_MappingContainer = new HorizontalPanel();

        hp_MappingContainer.setHeight("50");
		/*
		 * Mapping label
		 */
        Label label_mapping = new Label();
        label_mapping.setText("Mapping:");
        label_mapping.setStyleName("label");
        label_mapping.setHeight("26px");
        hp_MappingContainer.add(label_mapping);
        hp_MappingContainer.setCellVerticalAlignment(label_mapping, HasVerticalAlignment.ALIGN_MIDDLE);
        hp_MappingContainer.setCellHorizontalAlignment(label_mapping, HasHorizontalAlignment.ALIGN_CENTER);
        hp_MappingContainer.setCellHeight(label_mapping, "50px");
        hp_MappingContainer.setCellWidth(label_mapping, "40px");
		
		/*
		 * Mapping Button
		 */
        SimplePanel sp_mappingButtonContainer = new SimplePanel();
        sp_mappingButtonContainer.setStyleName("wrapper");
        this.createbtn = new Button();
        this.createbtn.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                onCreateBtnClick(event);
            }
        });
        this.createbtn.setStyleName("button");
        this.createbtn.setWidth("100%");
        this.createbtn.setHeight("100%");
        String createBtnHtml = "<img src='imgs/SourceandTarget/share.svg' title='Create Mapping' class='icon'/><div><label>Map</label></div>";
        this.createbtn.setHTML(createBtnHtml);

        sp_mappingButtonContainer.add(this.createbtn);
        hp_MappingContainer.add(sp_mappingButtonContainer);
		/*
		 * Add Mapping in Item Container
		 */
        hp_ItemContainer.add(hp_MappingContainer);
        hp_ItemContainer.setCellVerticalAlignment(hp_MappingContainer, HasVerticalAlignment.ALIGN_MIDDLE);
		
		/*
		 * Cell for keeping Config ListBox and label
		 */
        HorizontalPanel hp_ConfigContainer = new HorizontalPanel();

        hp_ConfigContainer.setHeight("50");
		/*
		 * Config label
		 */
        Label label_config = new Label();
        label_config.setText("Configuration");
        label_config.setStyleName("label");
        label_config.setHeight("26px");
        hp_ConfigContainer.add(label_config);
        hp_ConfigContainer.setCellVerticalAlignment(label_config, HasVerticalAlignment.ALIGN_MIDDLE);
        hp_ConfigContainer.setCellHorizontalAlignment(label_config, HasHorizontalAlignment.ALIGN_CENTER);
        hp_ConfigContainer.setCellHeight(label_config, "50px");
        hp_ConfigContainer.setCellWidth(label_config, "40px");
		
		/*
		 * Add Config ListBox, which has already been created by the createMatchComboBox()
		 */
        hp_ConfigContainer.add(this.cbConfig);
        hp_ConfigContainer.setCellVerticalAlignment(this.cbConfig, HasVerticalAlignment.ALIGN_MIDDLE);
        hp_ConfigContainer.setCellHorizontalAlignment(this.cbConfig, HasHorizontalAlignment.ALIGN_CENTER);
        hp_ConfigContainer.setCellHeight(this.cbConfig, "50px");
        hp_ConfigContainer.setCellWidth(this.cbConfig, "40px");
		
		/*
		 * Add Config Container in Item Container
		 */
        hp_ItemContainer.add(hp_ConfigContainer);
        hp_ItemContainer.setCellVerticalAlignment(hp_ConfigContainer, HasVerticalAlignment.ALIGN_MIDDLE);
		
		/*
		 * S-Match Icon
		 */
		/*
		 * Cell for keeping S-Match icon and label
		 */
        HorizontalPanel hp_IconContainer = new HorizontalPanel();

        hp_ConfigContainer.setHeight("105px");

        VerticalPanel vp_IconContainer = new VerticalPanel();
		/*
		 * S-Match icon
		 */


        Image appIcon = new Image();
        appIcon.setUrl("imgs/s-match.svg");
        appIcon.setHeight("62px");
        appIcon.setAltText("S-Match Web");
        appIcon.setWidth("70px");

        vp_IconContainer.add(appIcon);
        vp_IconContainer.setCellVerticalAlignment(appIcon, HasVerticalAlignment.ALIGN_MIDDLE);
        vp_IconContainer.setCellHorizontalAlignment(appIcon, HasHorizontalAlignment.ALIGN_CENTER);
		
		/*
		 * app label
		 */
        Label appLabel = new Label();
        appLabel.setText("S-Match");

        vp_IconContainer.add(appLabel);
        vp_IconContainer.setCellVerticalAlignment(appLabel, HasVerticalAlignment.ALIGN_MIDDLE);
        vp_IconContainer.setCellHorizontalAlignment(appLabel, HasHorizontalAlignment.ALIGN_CENTER);

		
		
		/*
		 * Add Vertical Panel in horizontal icon container
		 */
        hp_IconContainer.add(vp_IconContainer);
		/*
		 * Add icon Container in Item Container
		 */
        hp_ItemContainer.add(hp_IconContainer);
        hp_ItemContainer.setCellVerticalAlignment(hp_IconContainer, HasVerticalAlignment.ALIGN_MIDDLE);


        hp_ItemContainer.setWidth("100%");
        hp_ItemContainer.setHeight("100%");
        hp_ItemContainer.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
        hp_ItemContainer.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        sp_GenericOptions.add(hp_ItemContainer);

        dlp_mainContainer.addNorth(sp_GenericOptions, 7.8);
		
		/*
		 * Central Tree and Table area
		 */
        SplitLayoutPanel spl_CentralControl = new SplitLayoutPanel();
		
		/*
		 * CellTable Container
		 */
        SimplePanel sp_ctContainer = new SimplePanel();
		
		/*
		 * Scroll panel for cellTable
		 */
        ScrollPanel scrp_cellTableScroller = new ScrollPanel();
        scrp_cellTableScroller.setHeight("350px");
		/*
		 * Add the celltable in simple panel
		 */
        scrp_cellTableScroller.add(this.cellTable);
        this.cellTable.setStyleName("table");
		
		/*
		 * Add the scroll panel in simple panel
		 */
        sp_ctContainer.add(scrp_cellTableScroller);


        sp_ctContainer.setWidth("100%");
		
		/*
		 * Add the simple panel at the bottom of the central controller
		 */
        spl_CentralControl.addSouth(sp_ctContainer, 460);
		
		/*
		 * Creating the central area for celltrees
		 */
        HorizontalSplitPanel hsp_centralArea = new HorizontalSplitPanel();
		
		/*
		 * Create the source celltree area
		 */
        SimplePanel sp_SourceContainer = new SimplePanel();

        DockLayoutPanel dlp_SourceContainer = new DockLayoutPanel(Unit.PX);

        SimplePanel sp_SourceButtons = new SimplePanel();
        HorizontalPanel hp_SourceButtons = new HorizontalPanel();
		/*
		 * Create New Source Tree
		 */
        SimplePanel sp_CreateSource = new SimplePanel();
        this.createSource = new Button();
        this.createSource.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                onCreateSourceClick(event);
            }
        });
        this.createSource.setStyleName("button");
        String createSourceHtml = "<img src='imgs/SourceandTarget/document-new.svg' class='icon'" +
                "title='Create Source Tree' /><div><label>New</label></div>";

        this.createSource.setHTML(createSourceHtml);

        sp_CreateSource.add(this.createSource);
        sp_CreateSource.setStyleName("wrapper");
		/*
		 * Create Add Source Node
		 */
        SimplePanel sp_AddSourceNode = new SimplePanel();
        this.addSourceNode = new Button();
        this.addSourceNode.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                onAddSourceNodeClick(event);
            }
        });
        this.addSourceNode.setStyleName("button");
        this.addSourceNode.setEnabled(false);
        String addSourceNodeHtml = "<img src='imgs/SourceandTarget/list-add.svg' title='Add Node' class='icon' />" +
                "<div><label>Add Node</label></div>";

        this.addSourceNode.setHTML(addSourceNodeHtml);

        sp_AddSourceNode.add(this.addSourceNode);
        sp_AddSourceNode.setStyleName("wrapper");
		/*
		 * Create Add Source Node Child
		 */
        SimplePanel sp_AddSourceChildNode = new SimplePanel();
        this.addSourceChildNode = new Button();
        this.addSourceChildNode.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                onAddSourceChildNodeClick(event);
            }
        });
        this.addSourceChildNode.setStyleName("button");
        this.addSourceChildNode.setEnabled(false);
        String addSourceChildNodeHtml = "<img src='imgs/SourceandTarget/addchild.svg' title='Add Child Node'  class='icon'/>" +
                "<div><label>Add ChildNode</label></div>";

        this.addSourceChildNode.setHTML(addSourceChildNodeHtml);

        sp_AddSourceChildNode.add(this.addSourceChildNode);
        sp_AddSourceChildNode.setStyleName("wrapper");
		/*
		 * Create Delete Source Node
		 */
        SimplePanel sp_DeleteSourceNode = new SimplePanel();
        this.deleteSourceNode = new Button();
        this.deleteSourceNode.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                onDeleteSourceNodeClick(event);
            }
        });
        this.deleteSourceNode.setStyleName("button");
        this.deleteSourceNode.setEnabled(false);
        String deleteSourceNodeHtml = "<img src='imgs/SourceandTarget/list-remove.svg' title='Delete Node' class='icon'/>" +
                "<div><label>Delete Node</label></div>";

        this.deleteSourceNode.setHTML(deleteSourceNodeHtml);

        sp_DeleteSourceNode.add(this.deleteSourceNode);
        sp_DeleteSourceNode.setStyleName("wrapper");
		
		/*
		 * Add source buttons in the horizontal area above the tree
		 */
        hp_SourceButtons.add(sp_CreateSource);
        hp_SourceButtons.add(sp_AddSourceNode);
        hp_SourceButtons.add(sp_AddSourceChildNode);
        hp_SourceButtons.add(sp_DeleteSourceNode);

        hp_SourceButtons.setWidth("100%");
        hp_SourceButtons.setHeight("60px");
        hp_SourceButtons.setStyleName("sub_header");
        hp_SourceButtons.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);


        sp_SourceButtons.add(hp_SourceButtons);
        dlp_SourceContainer.addNorth(sp_SourceButtons, 62);
		
		/*
		 * Creating real Source Tree area now
		 */
        this.treeSourceOptionTabs = new TabPanel();

        this.treeSourceOptionTabs.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                onTreeSourceOptionTabsSelection(event);
            }
        });



        ScrollPanel scrp_TreeSourceScroller = new ScrollPanel();
        this.cellTreeSource.setStyleName("treeBox");
        this.cellTreeSource.addStyleName("{style.tree}");
        this.cellTreeSource.setWidth("98%");
        this.cellTreeSource.setHeight("300px");
        this.cellTreeSource.setFocus(true);
        scrp_TreeSourceScroller.add(this.cellTreeSource);

        this.treeSourceOptionTabs.add(scrp_TreeSourceScroller, "Graphical Tree");

		/*
		 * Creating Source Text area
		 */


        VerticalPanel vp_SourceTextArea = new VerticalPanel();
        vp_SourceTextArea.setHeight("100%");
        vp_SourceTextArea.setWidth("99%");
        this.textTreeSource = new TextArea();
        this.textTreeSource.setWidth("98%");
        this.textTreeSource.setHeight("206px");
        this.textTreeSource.setStyleName("treeBox");
        this.textTreeSource.setVisibleLines(20);
        vp_SourceTextArea.add(this.textTreeSource);

        this.treeSourceOptionTabs.add(vp_SourceTextArea, "Textual Tree");

        this.treeSourceOptionTabs.setWidth("100%");
        this.treeSourceOptionTabs.setHeight("100%");
        dlp_SourceContainer.add(this.treeSourceOptionTabs);

        dlp_SourceContainer.setHeight("100%");
        dlp_SourceContainer.setWidth("100%");
        sp_SourceContainer.add(dlp_SourceContainer);

        sp_SourceContainer.setHeight("100%");
        sp_SourceContainer.setStyleName("tree");
		
		/*
		 * Add the source container in the central area
		 */
        hsp_centralArea.add(sp_SourceContainer);
		
		

		/*
		 * Create the target celltree area
		 */
        SimplePanel sp_TargetContainer = new SimplePanel();


        DockLayoutPanel dlp_TargetContainer = new DockLayoutPanel(Unit.PX);

        SimplePanel sp_TargetButtons = new SimplePanel();
        HorizontalPanel hp_TargetButtons = new HorizontalPanel();
		/*
		 * Create New Target Tree
		 */
        SimplePanel sp_CreateTarget = new SimplePanel();
        this.createTarget = new Button();
        this.createTarget.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                onCreateTargetClick(event);
            }
        });
        this.createTarget.setStyleName("button");
        String createTargetHtml = "<img src='imgs/SourceandTarget/document-new.svg' class='icon'" +
                "title='Create Target Tree' /><div><label>New</label></div>";

        this.createTarget.setHTML(createTargetHtml);

        sp_CreateTarget.add(this.createTarget);
        sp_CreateTarget.setStyleName("wrapper");
		/*
		 * Create Add Target Node
		 */
        SimplePanel sp_AddTargetNode = new SimplePanel();
        this.addTargetNode = new Button();
        this.addTargetNode.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                onAddTargetNodeClick(event);
            }
        });
        this.addTargetNode.setStyleName("button");
        this.addTargetNode.setEnabled(false);
        String addTargetNodeHtml = "<img src='imgs/SourceandTarget/list-add.svg' title='Add Node' class='icon' />" +
                "<div><label>Add Node</label></div>";

        this.addTargetNode.setHTML(addTargetNodeHtml);

        sp_AddTargetNode.add(this.addTargetNode);
        sp_AddTargetNode.setStyleName("wrapper");
		/*
		 * Create Add Target Node Child
		 */
        SimplePanel sp_AddTargetChildNode = new SimplePanel();
        this.addTargetChildNode = new Button();
        this.addTargetChildNode.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                onAddTargetChildNodeClick(event);
            }
        });
        this.addTargetChildNode.setStyleName("button");
        this.addTargetChildNode.setEnabled(false);
        String addTargetChildNodeHtml = "<img src='imgs/SourceandTarget/addchild.svg' title='Add Child Node'  class='icon'/>" +
                "<div><label>Add ChildNode</label></div>";

        this.addTargetChildNode.setHTML(addTargetChildNodeHtml);

        sp_AddTargetChildNode.add(this.addTargetChildNode);
        sp_AddTargetChildNode.setStyleName("wrapper");
		/*
		 * Create Delete Target Node
		 */
        SimplePanel sp_DeleteTargetNode = new SimplePanel();
        this.deleteTargetNode = new Button();
        this.deleteTargetNode.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                onDeleteTargetNodeClick(event);
            }
        });
        this.deleteTargetNode.setStyleName("button");
        this.deleteTargetNode.setEnabled(false);
        String deleteTargetNodeHtml = "<img src='imgs/SourceandTarget/list-remove.svg' title='Delete Node' class='icon'/>" +
                "<div><label>Delete Node</label></div>";

        this.deleteTargetNode.setHTML(deleteTargetNodeHtml);

        sp_DeleteTargetNode.add(this.deleteTargetNode);
        sp_DeleteTargetNode.setStyleName("wrapper");
		
		/*
		 * Add target buttons in the horizontal area above the tree
		 */
        hp_TargetButtons.add(sp_CreateTarget);
        hp_TargetButtons.add(sp_AddTargetNode);
        hp_TargetButtons.add(sp_AddTargetChildNode);
        hp_TargetButtons.add(sp_DeleteTargetNode);

        hp_TargetButtons.setWidth("100%");
        hp_TargetButtons.setHeight("60px");
        hp_TargetButtons.setStyleName("sub_header");
        hp_TargetButtons.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);


        sp_TargetButtons.add(hp_TargetButtons);
        dlp_TargetContainer.addNorth(sp_TargetButtons, 62);
		
		/*
		 * Creating real Target Tree area now
		 */
        this.treeTargetOptionTabs = new TabPanel();
        this.treeTargetOptionTabs.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                onTreeTargetOptionTabsSelection(event);
            }
        });


        ScrollPanel scrp_TreeTargetScroller = new ScrollPanel();
        this.cellTreeTarget.setStyleName("treeBox");
        this.cellTreeTarget.addStyleName("{style.tree}");
        this.cellTreeTarget.setWidth("98%");
        this.cellTreeTarget.setHeight("300px");
        this.cellTreeTarget.setFocus(true);
        scrp_TreeTargetScroller.add(this.cellTreeTarget);

        this.treeTargetOptionTabs.add(scrp_TreeTargetScroller, "Graphical Tree");

		/*
		 * Creating Target Text area now
		 */

        VerticalPanel vp_TargetTextArea = new VerticalPanel();
        vp_TargetTextArea.setHeight("100%");
        vp_TargetTextArea.setWidth("99%");
        this.textTreeTarget = new TextArea();
        this.textTreeTarget.setWidth("98%");
        this.textTreeTarget.setHeight("206px");
        this.textTreeTarget.setStyleName("treeBox");
        this.textTreeTarget.setVisibleLines(20);
        vp_TargetTextArea.add(this.textTreeTarget);

        this.treeTargetOptionTabs.add(vp_TargetTextArea, "Textual Tree");


        this.treeTargetOptionTabs.setWidth("100%");
        this.treeTargetOptionTabs.setHeight("100%");
        dlp_TargetContainer.add(this.treeTargetOptionTabs);


        dlp_TargetContainer.setHeight("100%");
        dlp_TargetContainer.setWidth("100%");

        sp_TargetContainer.add(dlp_TargetContainer);

        sp_TargetContainer.setHeight("100%");
        sp_TargetContainer.setStyleName("tree");
		
		
		/*
		 * Add the target container in the central area
		 */
        hsp_centralArea.add(sp_TargetContainer);
		
		/*
		 * Add the horizontalSplitPanel at the center of the central controller
		 */
        spl_CentralControl.add(hsp_centralArea);

        spl_CentralControl.setHeight("800px");
        spl_CentralControl.setWidth("100%");
        dlp_mainContainer.add(spl_CentralControl);

        RootPanel.get().add(dlp_mainContainer);
		/*
		 * Everything exists, so now we can refresh the textareas for source and
		 * target To have the default data in them.
		 */
        refreshSourceText();
        ((CustomTreeModel) cellTreeSource.getTreeViewModel()).addButtonRef(
                this.addSourceNode, this.deleteSourceNode,
                this.addSourceChildNode);
        ((CustomTreeModel) cellTreeTarget.getTreeViewModel()).addButtonRef(
                this.addTargetNode, this.deleteTargetNode,
                this.addTargetChildNode);
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
        cellTable = new CellTable<MatchLog>(1, resource);

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

		/*
		 * Relation cell column.$
		 */

        RelationCell relationCell = new RelationCell();
        Column<MatchLog, String> relationColumn = new Column<MatchLog, String>(
                relationCell) {
            @Override
            public String getValue(MatchLog match) {
                return match.getRelation();
            }

        };
        relationColumn
                .setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

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

        // Add the columns.

        SafeHtmlHeader sourceHeader = new SafeHtmlHeader(new SafeHtml() {

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
    void onCreateBtnClick(ClickEvent event) {
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
		 * Match the two models
		 */
        matchModels();

    }

    /*
     * Match elements of two lists. $
     */
    private void matchModels() {

        serverComUI = new MappingReporter();
        serverComUI.show(this);


        BaseContextPair contextPair = new BaseContextPair();

        SMatchWeb.BaseContextPairMapper mapper = GWT.create(SMatchWeb.BaseContextPairMapper.class);

        SMatchTree smtSource = new SMatchTree();

        BaseContext sourceContext = smtSource.write((CustomTreeModel) this.cellTreeSource.getTreeViewModel());
        contextPair.setSourceContext(sourceContext);

        SMatchTree smtTarget = new SMatchTree();

        BaseContext targetContext = smtTarget.write((CustomTreeModel) this.cellTreeTarget.getTreeViewModel());
        contextPair.setTargetContext(targetContext);

        String json = mapper.write(contextPair);

        GWT.log(json);
        //serverComUI.setTextToServerLabel(json);
        // further see example at https://github.com/nmorel/gwt-jackson
        String url = "http://localhost:8080/webapi/match/default";
        RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, URL.encode(url));

        try {
            builder.setHeader("Content-Type", "application/json; charset=utf-8");
            Request request = builder.sendRequest(json, new RequestCallback() {
                public void onError(Request request, Throwable exception) {
                    // Couldn't connect to server (could be timeout, SOP violation, etc.)
                    // Show the RPC error message to the user
                    serverComUI.setServerResponseLabel("RPC Error");
                    serverComUI.setServerResponseText("Unable to make the call");
                }

                public void onResponseReceived(Request request, Response response) {
                    if (200 == response.getStatusCode()) {
                        // Process the response in response.getText()
                        serverComUI.setServerResponseLabel(response.getStatusCode() + " " + response.getStatusText());
                        serverComUI.setServerResponseText(response.getText());

                    } else {
                        // Handle the error.  Can get the status text from response.getStatusText()
                        serverComUI.setServerResponseLabel(response.getStatusCode() + " " + response.getStatusText());
                        serverComUI.setServerResponseText(response.getText());

                    }
                }
            });
        } catch (RequestException e) {
            showError("Exception while connecting with the server:"+e.getMessage());
            // Couldn't connect to server
        }

    }

    void refreshSourceText() {
        this.textTreeSource.setVisible(false);

        CustomTreeModel model = (CustomTreeModel) this.cellTreeSource
                .getTreeViewModel();
        AsterTree atree = new AsterTree();
        this.textTreeSource.setText(atree.write(model));
        this.textTreeSource.setVisible(true);

    }

    void refreshSourceTree() {

        String ext_treeData = textTreeSource.getText();
        if (!validateText(ext_treeData)) {
            return;
        }

        ext_treeData = ext_treeData.trim();

        // Create a model for the tree.
        CustomTreeModel model = (CustomTreeModel) this.cellTreeSource
                .getTreeViewModel();
        model.clearAll(null);

        AsterTree atree = new AsterTree();

        atree.read(ext_treeData, model);

        model.refresh();
        this.TreeNodeExpand(this.cellTreeSource.getRootTreeNode(), "");

    }

    public void TreeNodeExpand(TreeNode node, String nodeId) {
        if (nodeId == null || nodeId.equalsIgnoreCase("")) {
            for (int i = 0; i < node.getChildCount(); i++) {
                if (!node.isChildLeaf(i)) {
                    TreeNodeExpand(node.setChildOpen(i, true), "");
                }
            }
        } else {
            for (int i = 0; i < node.getChildCount(); i++) {
                TNode currNode = (TNode) node.getChildValue(i);

                if (currNode.getid().equalsIgnoreCase(nodeId)) {
                    TreeNodeExpand(node.setChildOpen(i, true), "");
                    return;
                }
                TNode found = currNode.getChildById(nodeId);

                if (found != null) {
                    TreeNodeExpand(node.setChildOpen(i, true), nodeId);
                    break;
                }

            }
        }

    }


    public void TreeNodeCollapse(TreeNode node) {
        for (int i = 0; i < node.getChildCount(); i++) {
            if (!node.isChildLeaf(i)) {
                TreeNodeCollapse(node.setChildOpen(i, false));
            }

        }

    }


    void refreshTargetText() {
        CustomTreeModel model = (CustomTreeModel) this.cellTreeTarget
                .getTreeViewModel();
        AsterTree atree = new AsterTree();

        this.textTreeTarget.setText(atree.write(model));

        this.textTreeTarget.setVisible(true);
    }

    void refreshTargetTree() {
        String ext_treeData = textTreeTarget.getText();

        if (!validateText(ext_treeData)) {
            return;
        }
        ext_treeData = ext_treeData.trim();

        // Create a model for the tree.
        CustomTreeModel model = (CustomTreeModel) this.cellTreeTarget
                .getTreeViewModel();
        model.clearAll(null);

        AsterTree atree = new AsterTree();

        atree.read(ext_treeData, model);

        model.refresh();
        this.TreeNodeExpand(this.cellTreeTarget.getRootTreeNode(), "");
    }

    @UiHandler("createSource")
    void onCreateSourceClick(ClickEvent event) {
        CustomTreeModel model = (CustomTreeModel) this.cellTreeSource
                .getTreeViewModel();
        model.clearAll(null);
        model.createDefault(null);
        model.refresh();
        this.TreeNodeCollapse(this.cellTreeSource.getRootTreeNode());

    }

    @UiHandler("createTarget")
    void onCreateTargetClick(ClickEvent event) {
        CustomTreeModel model = (CustomTreeModel) this.cellTreeTarget
                .getTreeViewModel();
        model.clearAll(null);
        model.createDefault(null);
        model.refresh();
        this.TreeNodeCollapse(this.cellTreeTarget.getRootTreeNode());

    }

    void showError(String errorStr) {
        msg = new MsgPopUp(errorStr);
        msg.show();
    }

    @UiHandler("treeTargetOptionTabs")
    void onTreeTargetOptionTabsSelection(SelectionEvent<Integer> event) {
        if (event.getSelectedItem() == 1) {

            refreshTargetText();

            addTargetNode.setEnabled(false);
            deleteTargetNode.setEnabled(false);
            addTargetChildNode.setEnabled(false);

        }
        if (event.getSelectedItem() == 0) {

            refreshTargetTree();

            if (notTargetEmpty) {
                addTargetNode.setEnabled(true);
                deleteTargetNode.setEnabled(true);
                addTargetChildNode.setEnabled(true);
            }

        }

    }


    @UiHandler("treeSourceOptionTabs")
    void onTreeSourceOptionTabsSelection(SelectionEvent<Integer> event) {
        // Tree area for Source selected
        if (event.getSelectedItem() == 0) {


            refreshSourceTree();

            if (notSourceEmpty) {
                addSourceNode.setEnabled(true);
                deleteSourceNode.setEnabled(true);
                addSourceChildNode.setEnabled(true);
            }

        }
        // Text area for source is selected
        if (event.getSelectedItem() == 1) {

            refreshSourceText();

            addSourceNode.setEnabled(false);
            deleteSourceNode.setEnabled(false);
            addSourceChildNode.setEnabled(false);

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

        model.createDefault(selNode);
        this.TreeNodeExpand(this.cellTreeSource.getRootTreeNode(), selNode.getid());
        model.refresh();
        notSourceEmpty = true;
        if (!addSourceNode.isEnabled()) {
            addSourceNode.setEnabled(true);
        }
        if (!deleteSourceNode.isEnabled()) {
            deleteSourceNode.setEnabled(true);
        }
    }

    @UiHandler("deleteSourceNode")
    void onDeleteSourceNodeClick(ClickEvent event) {
        CustomTreeModel model = (CustomTreeModel) this.cellTreeSource
                .getTreeViewModel();
        TNode selNode = model.getSelectionModel().getSelectedObject();

        if (selNode != null) {

            if (selNode.getParent() != null) {
                selNode.clearAll();

                selNode.getParent().removeChild(selNode);

            } else {
                showError("Cannot delete Root.");
            }
        }

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
        model.createDefault(selNode);
        this.TreeNodeExpand(this.cellTreeTarget.getRootTreeNode(), selNode.getid());
        model.refresh();
        notTargetEmpty = true;
        if (!addTargetNode.isEnabled()) {
            addTargetNode.setEnabled(true);
        }
        if (!deleteTargetNode.isEnabled()) {
            deleteTargetNode.setEnabled(true);
        }

    }

    @UiHandler("deleteTargetNode")
    void onDeleteTargetNodeClick(ClickEvent event) {
        CustomTreeModel model = (CustomTreeModel) this.cellTreeTarget
                .getTreeViewModel();
        TNode selNode = model.getSelectionModel().getSelectedObject();
        if (selNode != null) {
            selNode.clearAll();
            if (selNode.getParent() != null) {
                selNode.getParent().removeChild(selNode);
            } else {
                showError("Cannot delete Root.");
            }
        }
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

    @Override
    public void progressComplete(String json) {
        SMatchWeb.MatchingTaskMapper mapper = GWT.create(SMatchWeb.MatchingTaskMapper.class);
        MatchingTask task = mapper.read(json);

        ArrayList<MatchLog> relatedNodes = new ArrayList<MatchLog>();

        NodesMatrixMapping matrixMapping = task.getResult();

        BaseNode sourceRoot = matrixMapping.getSourceContext().getRoot();

        /*
         * Recursively fetch mappings from each source node to each target node
         */
        getMapping(sourceRoot,relatedNodes,matrixMapping);

        /*
		 * Display the related models in the table.
		 */
        cellTable.setRowData(relatedNodes);

    }

    private void getMapping(BaseNode sourceNode, ArrayList<MatchLog> relatedNodes, NodesMatrixMapping matrixMapping) {

        Set<MappingElement> relationMaps = matrixMapping.getSources(sourceNode);

        for (Iterator<MappingElement> Ite_relation = relationMaps.iterator(); Ite_relation.hasNext(); ) {
            MappingElement currRelation = Ite_relation.next();

            char relation = currRelation.getRelation();
            if(relation == '\0'){
                continue;
            }else{
                MatchLog matchedCellTableElement = new MatchLog();
                matchedCellTableElement.setSource(currRelation.getSource().getName());
                matchedCellTableElement.setTarget(currRelation.getTarget().getName());
                matchedCellTableElement.setRelation(relation);
                relatedNodes.add(matchedCellTableElement);

            }

        }
        for(Iterator<BaseNode> Ite_sourceNode = sourceNode.childrenIterator(); Ite_sourceNode.hasNext();){
            BaseNode nextSourceNode = Ite_sourceNode.next();
            getMapping(nextSourceNode,relatedNodes,matrixMapping);
        }
    }

    @Override
    public void progressHalted() {
        //serverComUI.hide();
    }
}
