package de.uniks.pmws2021.chat.controller;

import de.uniks.pmws2021.chat.ChatEditor;
import de.uniks.pmws2021.chat.controller.subcontroller.ClientViewSubController;
import de.uniks.pmws2021.chat.controller.subcontroller.ServerViewSubController;
import de.uniks.pmws2021.chat.model.User;
import de.uniks.pmws2021.chat.util.ResourceManager;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

public class StartViewController {

    private final Parent view;
    private final ChatEditor editor;
    private Button serverButton;
    private Button clientButton;
    private ServerViewSubController serverViewSubController;
    private ClientViewSubController clientViewSubController;

    public StartViewController(Parent view, ChatEditor editor) {
        this.view = view;
        this.editor = editor;
    }

    public void init() {
        // Load all view references
        serverButton = (Button) view.lookup("#ServerButton");
        clientButton = (Button) view.lookup("#ClientButton");

        // on  mouse action
        serverButton.setOnAction(this::serverButtonOnClick);
        clientButton.setOnAction(this::clientButtonOnClick);

        // init subcontroller
        clientViewSubController = new ClientViewSubController(view, editor);
        serverViewSubController = new ServerViewSubController(view, editor);

        // init views

        // load users
        loadUserHelper();

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
        // ToDo: initialize controller in here
        dialog.showAndWait().ifPresent(this.editor::haveUser);
        // some dummies
        this.editor.createDummies();
        clientViewSubController.init();
    }
    private void serverButtonOnClick(ActionEvent event) {
        serverViewSubController.init();
    }

    private void loadUserHelper() {
        for (User user:ResourceManager.loadServerUsers()
        ) {
            this.editor.haveUser(user.getName());
        }
    }

}
