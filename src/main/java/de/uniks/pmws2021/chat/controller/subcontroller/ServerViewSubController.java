package de.uniks.pmws2021.chat.controller.subcontroller;

import de.uniks.pmws2021.chat.ChatEditor;
import de.uniks.pmws2021.chat.StageManager;
import de.uniks.pmws2021.chat.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

public class ServerViewSubController {
    private Parent view;
    private ChatEditor editor;
    private Stage stage;
    private Button closeServerButton;
    private Button disconnectOneButton;
    private Button disconnectAllButton;
    private ListView<User> memberListView;

    public ServerViewSubController(Parent view, ChatEditor editor, Stage stage) {
        this.view = view;
        this.editor = editor;
        this.stage = stage;
    }

    public void init() {
        try {

            // set view
            view = FXMLLoader.load(StageManager.class.getResource("view/MiniChatServer.fxml"));
            Scene scene = new Scene(view);

            // display via StageManager
            stage.setTitle("PMWS2021 - Mini Chat::Server");
            stage.setScene(scene);

            // load all view references
            closeServerButton = (Button) view.lookup("#CloseServerButton");
            disconnectOneButton = (Button) view.lookup("#DisconnectOneButton");
            disconnectAllButton = (Button) view.lookup("#DisconnectAllButton");
            memberListView = (ListView<User>) view.lookup("#MemberListView");

            // set on mouse action
            closeServerButton.setOnAction(this::closeServerButtonOnClick);
            disconnectAllButton.setOnAction(this::disconnectAllButtonOnClick);
            disconnectOneButton.setOnAction(this::disconnectOneButtonOnClick);


        } catch (Exception e) {
            System.err.println("Failed to load Chat Server :: ServerViewSubController init()");
            e.printStackTrace();
        }

    }

    private void disconnectAllButtonOnClick(ActionEvent event) {
        System.out.println("Disconnect all Users");
    }

    private void disconnectOneButtonOnClick(ActionEvent event) {
        System.out.println("Disconnect one Users");
    }

    private void closeServerButtonOnClick(ActionEvent event) {
        System.out.println("Disconnect ServerView");
        StageManager.showMiniChatStart();
    }

}
