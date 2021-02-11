package de.uniks.pmws2021.chat.controller.subcontroller;

import de.uniks.pmws2021.chat.ChatEditor;
import de.uniks.pmws2021.chat.StageManager;
import de.uniks.pmws2021.chat.model.Chat;
import de.uniks.pmws2021.chat.model.User;
import de.uniks.pmws2021.chat.network.server.ChatServer;
import de.uniks.pmws2021.chat.util.ResourceManager;
import de.uniks.pmws2021.chat.view.chatListViewCellFactory;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

import java.beans.PropertyChangeEvent;
import java.util.List;

public class ServerViewSubController {
    private Chat model;
    private Parent view;
    private ChatEditor editor;
    private Button closeServerButton;
    private Button disconnectOneButton;
    private Button disconnectAllButton;
    private ListView<User> memberListView;
    private ChatServer chatServer;
    private User userData;
    private List<User> userList;
    private Object ListCell;

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
        memberListView.setCellFactory(new chatListViewCellFactory());

        // set on mouse action
        closeServerButton.setOnAction(this::closeServerButtonOnClick);
        disconnectAllButton.setOnAction(this::disconnectAllButtonOnClick);
        disconnectOneButton.setOnAction(this::disconnectOneButtonOnClick);
        memberListView.setOnMouseReleased(this::memberListViewOnClick);

        // ListView Init
        this.model.withAvailableUser(this.editor.getUserList()); // initialize
        memberListView.setItems(FXCollections.observableList(this.model.getAvailableUser()));

        // PCL
        // listener for every single user in list
        for (User user : this.editor.getUserList()
        ) {
            user.addPropertyChangeListener(user.PROPERTY_STATUS, this::onUserStatusChanged);
        }

        // ToDo listener for list
        model.addPropertyChangeListener(Chat.PROPERTY_AVAILABLE_USER, this::onUserListChanged);

        // ChatServer
        chatServer = new ChatServer(model, this.editor);

    }

    private void onUserListChanged(PropertyChangeEvent event) {
        memberListView.refresh();
    }

    private void onUserStatusChanged(PropertyChangeEvent event) {
        memberListView.refresh();
    }

    public void stop() {
        closeServerButton.setOnAction(null);
        disconnectAllButton.setOnAction(null);
        disconnectOneButton.setOnAction(null);

        for (User user : this.editor.getUserList()
        ) {
            user.removePropertyChangeListener(user.PROPERTY_STATUS, this::onUserStatusChanged);
        }

        chatServer.stopServer();
    }

    // ===========================================================================================
    // BUTTON ACTIONS
    // ===========================================================================================

    private void memberListViewOnClick(MouseEvent event) {
        if (event.getClickCount() == 1) {
            userData = memberListView.getFocusModel().getFocusedItem();
        }
    }

    private void disconnectAllButtonOnClick(ActionEvent event) {
        discoAll();
        System.out.println("Disconnect all Users");
    }

    private void disconnectOneButtonOnClick(ActionEvent event) {
        if (userData != null) {
            chatServer.disconnectUser(userData);
            System.out.println("Disconnect User: " + userData.getName());
        } else {
            Alert fail = new Alert(Alert.AlertType.WARNING);
            fail.setHeaderText("No user Selected");
            fail.setContentText("Please select user first");
            fail.showAndWait();
        }
    }

    private void closeServerButtonOnClick(ActionEvent event) {
        System.out.println("Disconnect ServerView");
        // disconnect all user
        discoAll();
        // save users
        saveUsersHelper();
        // show start screen
        StageManager.showMiniChatStart();
    }

    // ===========================================================================================
    // SUBCONTROLLER INIT & HELPER
    // ===========================================================================================

    private void discoAll() {
        for (User user : this.model.getAvailableUser()
        ) {
            if (user.getStatus()) {
                chatServer.disconnectUser(user);
            }
        }
    }

    private void saveUsersHelper() {
        for (User user : this.editor.getUserList()
        ) {
            ResourceManager.saveServerUsers(user);
        }
    }

}
