package de.uniks.pmws2021.chat.controller.subcontroller;

import com.sun.javafx.charts.Legend;
import de.uniks.pmws2021.chat.ChatEditor;
import de.uniks.pmws2021.chat.StageManager;
import de.uniks.pmws2021.chat.model.Chat;
import de.uniks.pmws2021.chat.model.User;
import de.uniks.pmws2021.chat.network.server.ChatServer;
import de.uniks.pmws2021.chat.util.ResourceManager;
import de.uniks.pmws2021.chat.view.chatListViewCellFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    private ChatServer chatServer;
    private ObservableList<User> usersObservableList;

    // ===========================================================================================
    // CONTROLLER
    // ===========================================================================================

    public ServerViewSubController(Parent view, ChatEditor editor) {
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

        // ListView Init
        usersObservableList = FXCollections.observableArrayList();
        // load users
        ResourceManager.loadServerUsers();
        // add to list
        usersObservableList.addAll(this.editor.getUserList());
        // ToDo: unsafe operation - fix this
        memberListView.setItems(usersObservableList);

        // PCL

        // ChatServer
        chatServer = new ChatServer(model, this.editor);

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
        // save users
        saveUsersHelper();
        // stop server
        chatServer.stopServer();
        // show start screen
        StageManager.showMiniChatStart();
    }

    // ===========================================================================================
    // SUBCONTROLLER INIT & HELPER
    // ===========================================================================================

    private void saveUsersHelper() {
        for (User user : this.editor.getUserList()
        ) {
            ResourceManager.saveServerUsers(user);
        }
    }

}
