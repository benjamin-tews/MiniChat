package de.uniks.pmws2021.chat.controller.subcontroller;

import de.uniks.pmws2021.chat.ChatEditor;
import de.uniks.pmws2021.chat.StageManager;
import de.uniks.pmws2021.chat.model.Chat;
import de.uniks.pmws2021.chat.model.User;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

public class ServerViewSubController {
    private Chat model;
    private Parent view;
    private ChatEditor editor;
    private Button closeServerButton;
    private Button disconnectOneButton;
    private Button disconnectAllButton;
    private ListView<User> memberListView;

    // ===========================================================================================
    // CONTROLLER
    // ===========================================================================================

    public ServerViewSubController(Chat model, Parent view, ChatEditor editor) {
        this.model = model;
        this.view = view;
        this.editor = editor;
    }

    public void init() {

        // load all view references
        closeServerButton = (Button) view.lookup("#CloseServerButton");
        disconnectOneButton = (Button) view.lookup("#DisconnectOneButton");
        disconnectAllButton = (Button) view.lookup("#DisconnectAllButton");
        memberListView = (ListView<User>) view.lookup("#MemberListView");

        // set on mouse action
        closeServerButton.setOnAction(this::closeServerButtonOnClick);
        disconnectAllButton.setOnAction(this::disconnectAllButtonOnClick);
        disconnectOneButton.setOnAction(this::disconnectOneButtonOnClick);

        // PCL

    }

    public void stop() {
        closeServerButton.setOnAction(null);
        disconnectAllButton.setOnAction(null);
        disconnectOneButton.setOnAction(null);
    }

    // ===========================================================================================
    // BUTTON ACTIONS
    // ===========================================================================================

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

    // ===========================================================================================
    // SUBCONTROLLER INIT & HELPER
    // ===========================================================================================

}
