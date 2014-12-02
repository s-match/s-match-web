package it.unitn.disi.smatch.web.client.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.http.client.*;
import com.google.gwt.user.client.ui.*;
import it.unitn.disi.smatch.web.client.SMatchWeb;
import it.unitn.disi.smatch.web.client.model.ServerTaskOpener;
import it.unitn.disi.smatch.web.shared.FieldVerifier;
import it.unitn.disi.smatch.web.shared.model.tasks.MatchingTask;

public class MappingReporter {
    private ServerTaskOpener opener = null;
    final DialogBox mr_modal;
    Label textToServerLabel;
    HTML serverResponseLabel;
    TextArea serverResponseText;

    public MappingReporter() {
        textToServerLabel = new Label();
        serverResponseLabel = new HTML();
        serverResponseText = new TextArea();

        mr_modal = createModal();
        mr_modal.setGlassEnabled(true);
        mr_modal.setAnimationEnabled(true);

    }

    public void setTextToServerLabel(String serverComm) {
        textToServerLabel.setText(serverComm);
    }

    public void setServerResponseLabel(String serverComm) {
        serverResponseLabel.setHTML(serverComm);
    }

    public void setServerResponseText(String serverComm) {
        serverResponseText.setText(serverComm);
    }

    public DialogBox getMr_modal() {
        return mr_modal;
    }

    public void show(final ServerTaskOpener parent) {
        hide();
        this.opener = parent;
        //mr_modal.center();
        mr_modal.show();
    }

    public void hide() {
        if (opener!=null)
            opener.progressHalted();
        mr_modal.hide();
    }

    /**
     * Create the dialog box for Error.
     *
     * @return the new dialog box
     */
    private DialogBox createModal() {

        final DialogBox dialogBox = new DialogBox();
        dialogBox.setText("Remote Procedure Call");
        dialogBox.setAnimationEnabled(true);
        final Button closeButton = new Button("Close");
        // We can set the id of a widget by accessing its Element
        closeButton.getElement().setId("closeButton");
        closeButton.addStyleName("button");

        final Button getTaskButton = new Button("Get Progress");
        getTaskButton.addStyleName("button");

        VerticalPanel dialogVPanel = new VerticalPanel();
        dialogVPanel.addStyleName("dialogVPanel");
        dialogVPanel.add(new HTML("<b>Data Sent to the server:</b>"));
        dialogVPanel.add(textToServerLabel);
        dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
        dialogVPanel.add(serverResponseLabel);
        HorizontalPanel taskHolder = new HorizontalPanel();
        taskHolder.add(new HTML("<br><b>Task:</b>"));
        taskHolder.add(serverResponseText);
        taskHolder.add(getTaskButton);
        dialogVPanel.add(taskHolder);
        dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
        dialogVPanel.add(closeButton);
        dialogBox.setWidget(dialogVPanel);

        //Add a handler to the getTask button
        // Add a handler to send the name to the server
        GetTaskHandler handler = new GetTaskHandler();
        getTaskButton.addClickHandler(handler);
        serverResponseText.addKeyUpHandler(handler);

        // Add a handler to close the DialogBox
        closeButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (opener!=null)
                    opener.progressHalted();
                mr_modal.hide();
            }
        });

        return dialogBox;
    }

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

            String textToServer = serverResponseText.getText();
            if (!FieldVerifier.isValidName(textToServer)) {
                setServerResponseLabel("<p>Please enter at least four characters</p>");
                return;
            }

            // Now we send the input to the server
            setTextToServerLabel(textToServer);
            setServerResponseLabel("");

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
                        setServerResponseLabel("Remote Procedure Call - Failure");
                        if (opener!=null)
                            opener.progressHalted();
                    }

                    public void onResponseReceived(Request request, Response response) {
                        if (200 == response.getStatusCode()) {
                            // Process the response in response.getText()
                            setServerResponseLabel("Remote Procedure Call:" +response.getStatusCode() + " " + response.getStatusText());
                            String json = response.getText();
                            if (opener!=null)
                                opener.progressComplete(json);
                            hide();

                        } else {
                            // Handle the error.  Can get the status text from response.getStatusText()
                            setServerResponseLabel("Remote Procedure Call - Failure:" + response.getStatusCode() + " " + response.getStatusText());
                            setServerResponseText(response.getText());
                            if (opener!=null)
                                opener.progressHalted();

                        }
                    }
                });
            } catch (RequestException e) {
                // Couldn't connect to server
                if (opener!=null)
                    opener.progressHalted();
            }
        }
    }


}
