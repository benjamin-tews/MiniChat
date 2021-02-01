package de.uniks.pmws2021.chat.controller;

import de.uniks.pmws2021.chat.ChatEditor;
import de.uniks.pmws2021.chat.controller.subcontroller.ClientViewSubController;
import de.uniks.pmws2021.chat.controller.subcontroller.ServerViewSubController;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

public class StartViewController {

    private final Parent view;
    private final ChatEditor editor;
    private Stage stage;
    private Button serverButton;
    private Button clientButton;
    private ServerViewSubController serverViewSubController;
    private ClientViewSubController clientViewSubController;

    public StartViewController(Parent view, ChatEditor editor, Stage stage) {
        this.view = view;
        this.editor = editor;
        this.stage = stage;
    }

    public void init() {
        // Load all view references
        serverButton = (Button) view.lookup("#ServerButton");
        clientButton = (Button) view.lookup("#ClientButton");

        // on  mouse action
        serverButton.setOnAction(this::serverButtonOnClick);
        clientButton.setOnAction(this::clientButtonOnClick);

        // init subcontroller
        clientViewSubController = new ClientViewSubController(view, editor, stage);
        serverViewSubController = new ServerViewSubController(view, editor, stage);

        // init views

    }

    public void stop() {
        // Clear references
        // Clear action listeners
    }

    // load views on mouse action
    private void clientButtonOnClick(ActionEvent event) {
        // Input Dialog
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New User");
        dialog.setHeaderText("Pls create a new user.");
        dialog.setContentText("Username: ");
        dialog.showAndWait().ifPresent(this.editor::haveUser);
        // some dummies
        this.editor.createDummies();

        clientViewSubController.init();
    }

    private void serverButtonOnClick(ActionEvent event) {
        serverViewSubController.init();
    }

}
