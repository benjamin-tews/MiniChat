package de.uniks.pmws2021.chat.controller;

import de.uniks.pmws2021.chat.ChatEditor;
import de.uniks.pmws2021.chat.StageManager;
import de.uniks.pmws2021.chat.controller.subcontroller.ClientViewSubController;
import de.uniks.pmws2021.chat.controller.subcontroller.ServerViewSubController;
import de.uniks.pmws2021.chat.model.Chat;
import de.uniks.pmws2021.chat.model.User;
import de.uniks.pmws2021.chat.network.server.ChatServer;
import de.uniks.pmws2021.chat.util.ResourceManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import java.io.IOException;
import java.util.ArrayList;

public class StartViewController {

    private final Parent view;
    private final ChatEditor editor;
    private Button serverButton;
    private Button clientButton;
    private ArrayList<ServerViewSubController> serverViewSubControllerList = new ArrayList<>();
    private ArrayList<ClientViewSubController> clientViewSubControllerList = new ArrayList<>();
    private ChatServer chatServer;

    // ===========================================================================================
    // CONTROLLER
    // ===========================================================================================

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

        // load users
        loadUsersHelper();

        // PCL

    }

    public void stop() {
        // Clear references
        serverButton.setOnAction(null);
        clientButton.setOnAction(null);

        // Clear action listeners

        // Stop SubController
        for (ServerViewSubController subController : serverViewSubControllerList
        ) {
            subController.stop();
        }

        for (ClientViewSubController subController : clientViewSubControllerList
        ) {
            subController.stop();
        }
    }

    // ===========================================================================================
    // BUTTON ACTIONS
    // ===========================================================================================

    private void clientButtonOnClick(ActionEvent event) {
        // Input Dialog
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New User");
        dialog.setHeaderText("Pls create a new user.");
        dialog.setContentText("Username: ");
        dialog.showAndWait().ifPresent(name -> initClientViewSubcontroller(this.editor.haveUser(name.toString(), "127.0.0.1")));
    }

    private void serverButtonOnClick(ActionEvent event) {
        initServerViewSubcontroller();
    }

    // ===========================================================================================
    // SUBCONTROLLER INIT & HELPER
    // ===========================================================================================

    // init method uses user from input field for initialize client controller model
    private void initClientViewSubcontroller(User user) {
        try {
            // set view
            Parent view = FXMLLoader.load(StageManager.class.getResource("view/MiniChatClient.fxml"));
            Scene scene = new Scene(view);

            // display Scene
            StageManager.stage.setTitle("PMWS2021 - Mini Chat::Client");
            StageManager.stage.setScene(scene);

            ClientViewSubController clientViewSubController = new ClientViewSubController(user, view, this.editor);
            clientViewSubController.init();

            // add subcontroller to list of controllers for removal
            clientViewSubControllerList.add(clientViewSubController);

        } catch (IOException e) {
            System.err.println("Failed to load Mini Chat Server View :: initServerViewSubController");
            e.printStackTrace();
        }

    }

    // initiate subcontroller for serverView
    private void initServerViewSubcontroller() {
        try {
            // set view
            Parent view = FXMLLoader.load(StageManager.class.getResource("view/MiniChatServer.fxml"));
            Scene scene = new Scene(view);

            // display Scene
            StageManager.stage.setTitle("PMWS2021 - Mini Chat::Server");
            StageManager.stage.setScene(scene);

            ServerViewSubController serverViewSubController = new ServerViewSubController(view, this.editor);
            serverViewSubController.init();

            // add subcontroller to list of controllers for removal
            serverViewSubControllerList.add(serverViewSubController);

        } catch (IOException e) {
            System.err.println("Failed to load Mini Chat Server View :: initServerViewSubController");
            e.printStackTrace();
        }
    }

    // save loaded user to actual userList
    private void loadUsersHelper() {
        for (User user : ResourceManager.loadServerUsers()
        ) {
            this.editor.haveUser(user.getName(), user.getIp()).setStatus(user.getStatus());
        }
    }

}
