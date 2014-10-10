package it.unitn.disi.smatch.web.client;

import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.http.client.*;
import com.google.gwt.user.client.ui.*;
import it.unitn.disi.smatch.web.shared.FieldVerifier;
import it.unitn.disi.smatch.web.shared.model.tasks.MatchingTask;
import it.unitn.disi.smatch.web.shared.model.trees.BaseContext;
import it.unitn.disi.smatch.web.shared.model.trees.BaseContextPair;
import it.unitn.disi.smatch.web.shared.model.trees.BaseNode;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class SMatchWeb implements EntryPoint {

    public static interface BaseContextPairMapper extends ObjectMapper<BaseContextPair> {}
    public static interface MatchingTaskMapper extends ObjectMapper<MatchingTask> {}

    /**
     * The message displayed to the user when the server cannot be reached or
     * returns an error.
     */
    private static final String SERVER_ERROR = "An error occurred while "
            + "attempting to contact the server. Please check your network "
            + "connection and try again.";

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        final Button getTaskButton = new Button("Get Task");
        final TextBox taskField = new TextBox();
        taskField.setText("Task ID");
        final Label errorLabel = new Label();

        // We can add style names to widgets
        getTaskButton.addStyleName("sendButton");

        final Button sendTaskButton = new Button("Send Task");
        sendTaskButton.addStyleName("sendButton");

        // Add the taskField and sendButton to the RootPanel
        // Use RootPanel.get() to get the entire body element
        RootPanel.get("taskFieldContainer").add(taskField);
        RootPanel.get("sendButtonContainer").add(getTaskButton);
        RootPanel.get("errorLabelContainer").add(errorLabel);

        RootPanel.get("sendTaskButtonContainer").add(sendTaskButton);

        // Focus the cursor on the name field when the app loads
        taskField.setFocus(true);
        taskField.selectAll();

        // Create the popup dialog box
        final DialogBox dialogBox = new DialogBox();
        dialogBox.setText("Remote Procedure Call");
        dialogBox.setAnimationEnabled(true);
        final Button closeButton = new Button("Close");
        // We can set the id of a widget by accessing its Element
        closeButton.getElement().setId("closeButton");
        final Label textToServerLabel = new Label();
        final HTML serverResponseLabel = new HTML();
        final TextArea serverResponseText = new TextArea();
        VerticalPanel dialogVPanel = new VerticalPanel();
        dialogVPanel.addStyleName("dialogVPanel");
        dialogVPanel.add(new HTML("<b>Sending name to the server:</b>"));
        dialogVPanel.add(textToServerLabel);
        dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
        dialogVPanel.add(serverResponseLabel);
        dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
        dialogVPanel.add(serverResponseText);
        dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
        dialogVPanel.add(closeButton);
        dialogBox.setWidget(dialogVPanel);

        // Add a handler to close the DialogBox
        closeButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                dialogBox.hide();
                getTaskButton.setEnabled(true);
                getTaskButton.setFocus(true);
                sendTaskButton.setEnabled(true);
            }
        });

        // Create a handler for the sendButton and taskField
        class GetTaskHandler implements ClickHandler, KeyUpHandler {
            /**
             * Fired when the user clicks on the sendButton.
             */
            public void onClick(ClickEvent event) {
                readTaskFromServer();
            }

            /**
             * Fired when the user types in the taskField.
             */
            public void onKeyUp(KeyUpEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    readTaskFromServer();
                }
            }

            /**
             * Send the name from the taskField to the server and wait for a response.
             */
            private void readTaskFromServer() {
                // First, we validate the input.
                errorLabel.setText("");
                String textToServer = taskField.getText();
                if (!FieldVerifier.isValidName(textToServer)) {
                    errorLabel.setText("Please enter at least four characters");
                    return;
                }

                // Then, we send the input to the server.
                getTaskButton.setEnabled(false);
                textToServerLabel.setText(textToServer);
                serverResponseLabel.setText("");

                // test for now
                // further see example at https://github.com/nmorel/gwt-jackson
                String url = "http://localhost:8080/webapi/tasks/" + textToServer;
                RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(url));

                try {
                    builder.setHeader("Content-Type", "application/json; charset=utf-8");
                    Request request = builder.sendRequest(null, new RequestCallback() {
                        public void onError(Request request, Throwable exception) {
                            // Couldn't connect to server (could be timeout, SOP violation, etc.)
                            // Show the RPC error message to the user
                            dialogBox.setText("Remote Procedure Call - Failure");
                            serverResponseLabel.addStyleName("serverResponseLabelError");
                            serverResponseLabel.setHTML(SERVER_ERROR);
                            dialogBox.center();
                            closeButton.setFocus(true);
                        }

                        public void onResponseReceived(Request request, Response response) {
                            if (200 == response.getStatusCode()) {
                                // Process the response in response.getText()
                                dialogBox.setText("Remote Procedure Call");
                                serverResponseLabel.removeStyleName("serverResponseLabelError");
                                serverResponseLabel.setHTML(response.getStatusCode() + " " + response.getStatusText());
                                MatchingTaskMapper mapper = GWT.create(MatchingTaskMapper.class);
                                String json = response.getText();
                                MatchingTask task = mapper.read(json);
                                serverResponseText.setText(mapper.write(task));
                                dialogBox.center();
                                closeButton.setFocus(true);
                            } else {
                                // Handle the error.  Can get the status text from response.getStatusText()
                                dialogBox.setText("Remote Procedure Call - Failure");
                                serverResponseLabel.addStyleName("serverResponseLabelError");
                                serverResponseLabel.setHTML(response.getStatusCode() + " " + response.getStatusText());
                                serverResponseText.setText(response.getText());


                                dialogBox.center();
                                closeButton.setFocus(true);
                            }
                        }
                    });
                } catch (RequestException e) {
                    // Couldn't connect to server
                }
            }
        }

        // Add a handler to send the name to the server
        GetTaskHandler handler = new GetTaskHandler();
        getTaskButton.addClickHandler(handler);
        taskField.addKeyUpHandler(handler);

        // Create a handler for the sendTaskButton
        class SendTaskHandler implements ClickHandler {
            /**
             * Fired when the user clicks on the sendTaskButton.
             */
            public void onClick(ClickEvent event) {
                sendTaskToServer();
            }

            /**
             * Send a fake matching task to the server and wait for a response.
             */
            private void sendTaskToServer() {
                // create fake task
                BaseContext source = new BaseContext();
                source.createRoot("Courses");
                BaseNode s1 = source.getRoot().createChild("College of Arts and Sciences");
                s1.createChild("Earth and Atmospheric Sciences");
                s1.createChild("Economics");
                s1.createChild("English");
                s1.createChild("Classics");
                s1.createChild("Asian Languages");
                s1.createChild("History");
                s1.createChild("Mathematics");
                s1.createChild("Astronomy");
                s1.createChild("Computer Science");
                s1.createChild("Linguistics");
                BaseNode s2 = source.getRoot().createChild("College of Engineering");
                s2.createChild("Chemical Engineering");
                s2.createChild("Civil and Environmental Engineering");
                s2.createChild("Electrical Computer Engineering");
                s2.createChild("Materials Science and Engineering");
                s2.createChild("Earth and Atmospheric Sciences");

                BaseContext target = new BaseContext();
                target.createRoot("Course");
                BaseNode t1 = target.getRoot().createChild("College of Arts and Sciences");
                t1.createChild("English");
                t1.createChild("Earth Sciences");
                t1.createChild("Computer Science");
                t1.createChild("Economics");
                t1.createChild("Astronomy");
                t1.createChild("Asian Languages");
                t1.createChild("Classics");
                t1.createChild("History");
                t1.createChild("Linguistics");
                t1.createChild("Mathematics");
                t1.createChild("History and Philosophy Science");
                BaseNode t2 = target.getRoot().createChild("College Engineering");
                t2.createChild("Civil and Environmental Engineering");
                t2.createChild("Electrical Engineering");
                t2.createChild("Chemical Engineering");
                t2.createChild("Materials Science and Engineering");

                BaseContextPair contextPair = new BaseContextPair();
                contextPair.setSourceContext(source);
                contextPair.setTargetContext(target);

                BaseContextPairMapper mapper = GWT.create(BaseContextPairMapper.class);
                //String json = "{\"sourceContext\":{\"root\":{\"children\":[{\"children\":[{\"children\":[],\"id\":\"n2_25210409\",\"name\":\"Earth and Atmospheric Sciences\"},{\"children\":[],\"id\":\"n3_25210409\",\"name\":\"Economics\"},{\"children\":[],\"id\":\"n4_25210409\",\"name\":\"English\"},{\"children\":[],\"id\":\"n5_25210409\",\"name\":\"Classics\"},{\"children\":[],\"id\":\"n6_25210409\",\"name\":\"Asian Languages\"},{\"children\":[],\"id\":\"n7_25210409\",\"name\":\"History\"},{\"children\":[],\"id\":\"n8_25210409\",\"name\":\"Mathematics\"},{\"children\":[],\"id\":\"n9_25210409\",\"name\":\"Astronomy\"},{\"children\":[],\"id\":\"n10_25210409\",\"name\":\"Computer Science\"},{\"children\":[],\"id\":\"n11_25210409\",\"name\":\"Linguistics\"}],\"id\":\"n1_25210409\",\"name\":\"College of Arts and Sciences\"},{\"children\":[{\"children\":[],\"id\":\"n13_25210409\",\"name\":\"Chemical Engineering\"},{\"children\":[],\"id\":\"n14_25210409\",\"name\":\"Civil and Environmental Engineering\"},{\"children\":[],\"id\":\"n15_25210409\",\"name\":\"Electrical Computer Engineering\"},{\"children\":[],\"id\":\"n16_25210409\",\"name\":\"Materials Science and Engineering\"},{\"children\":[],\"id\":\"n17_25210409\",\"name\":\"Earth and Atmospheric Sciences\"}],\"id\":\"n12_25210409\",\"name\":\"College of Engineering\"}],\"id\":\"n0_25210409\",\"name\":\"Courses\"}},\"targetContext\":{\"root\":{\"children\":[{\"children\":[{\"children\":[],\"id\":\"n20_25210409\",\"name\":\"English\"},{\"children\":[],\"id\":\"n21_25210409\",\"name\":\"Earth Sciences\"},{\"children\":[],\"id\":\"n22_25210409\",\"name\":\"Computer Science\"},{\"children\":[],\"id\":\"n23_25210409\",\"name\":\"Economics\"},{\"children\":[],\"id\":\"n24_25210409\",\"name\":\"Astronomy\"},{\"children\":[],\"id\":\"n25_25210409\",\"name\":\"Asian Languages\"},{\"children\":[],\"id\":\"n26_25210409\",\"name\":\"Classics\"},{\"children\":[],\"id\":\"n27_25210409\",\"name\":\"History\"},{\"children\":[],\"id\":\"n28_25210409\",\"name\":\"Linguistics\"},{\"children\":[],\"id\":\"n29_25210409\",\"name\":\"Mathematics\"},{\"children\":[],\"id\":\"n30_25210409\",\"name\":\"History and Philosophy Science\"}],\"id\":\"n19_25210409\",\"name\":\"College of Arts and Sciences\"},{\"children\":[{\"children\":[],\"id\":\"n32_25210409\",\"name\":\"Civil and Environmental Engineering\"},{\"children\":[],\"id\":\"n33_25210409\",\"name\":\"Electrical Engineering\"},{\"children\":[],\"id\":\"n34_25210409\",\"name\":\"Chemical Engineering\"},{\"children\":[],\"id\":\"n35_25210409\",\"name\":\"Materials Science and Engineering\"}],\"id\":\"n31_25210409\",\"name\":\"College Engineering\"}],\"id\":\"n18_25210409\",\"name\":\"Course\"}}}";
                String json = mapper.write(contextPair);
                GWT.log(json);

                errorLabel.setText("");

                // Then, we send the input to the server.
                sendTaskButton.setEnabled(false);
                //textToServerLabel.setText(json);
                serverResponseLabel.setText("");

                // test for now
                // further see example at https://github.com/nmorel/gwt-jackson
                String url = "http://localhost:8080/webapi/match/default";
                RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, URL.encode(url));

                try {
                    builder.setHeader("Content-Type", "application/json; charset=utf-8");
                    Request request = builder.sendRequest(json, new RequestCallback() {
                        public void onError(Request request, Throwable exception) {
                            // Couldn't connect to server (could be timeout, SOP violation, etc.)
                            // Show the RPC error message to the user
                            dialogBox.setText("Remote Procedure Call - Failure");
                            serverResponseLabel.addStyleName("serverResponseLabelError");
                            serverResponseLabel.setHTML(SERVER_ERROR);
                            dialogBox.center();
                            closeButton.setFocus(true);
                        }

                        public void onResponseReceived(Request request, Response response) {
                            if (200 == response.getStatusCode()) {
                                // Process the response in response.getText()
                                dialogBox.setText("Remote Procedure Call");
                                serverResponseLabel.removeStyleName("serverResponseLabelError");
                                serverResponseLabel.setHTML(response.getStatusCode() + " " + response.getStatusText());
                                serverResponseText.setText(response.getText());
                                taskField.setText(response.getText());
                                dialogBox.center();
                                closeButton.setFocus(true);
                            } else {
                                // Handle the error.  Can get the status text from response.getStatusText()
                                dialogBox.setText("Remote Procedure Call - Failure");
                                serverResponseLabel.addStyleName("serverResponseLabelError");
                                serverResponseLabel.setHTML(response.getStatusCode() + " " + response.getStatusText());
                                serverResponseText.setText(response.getText());
                                dialogBox.center();
                                closeButton.setFocus(true);
                            }
                        }
                    });
                } catch (RequestException e) {
                    // Couldn't connect to server
                }
            }
        }

        // Add a handler to send the name to the server
        SendTaskHandler sendTaskHandler = new SendTaskHandler();
        sendTaskButton.addClickHandler(sendTaskHandler);
    }
}
