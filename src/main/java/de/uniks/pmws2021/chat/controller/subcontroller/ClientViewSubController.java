package de.uniks.pmws2021.chat.controller.subcontroller;

import de.uniks.pmws2021.chat.ChatEditor;
import de.uniks.pmws2021.chat.StageManager;
import de.uniks.pmws2021.chat.model.User;
import de.uniks.pmws2021.chat.network.client.RestClient;
import de.uniks.pmws2021.chat.network.client.WebSocketClient;
import de.uniks.pmws2021.chat.util.JsonUtil;
import de.uniks.pmws2021.chat.view.chatListViewCellFactory;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.json.JSONObject;

import javax.json.JsonObject;
import javax.json.JsonStructure;
import java.beans.PropertyChangeEvent;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static de.uniks.pmws2021.chat.Constants.*;

public class ClientViewSubController {

    private User model;
    private Parent view;
    private ChatEditor editor;
    private Button leaveChatButton;
    private Button sendMsgButton;
    private TabPane chatBoxTabPane;
    private TextField inputTextField;
    private ListView<User> chatListView;
    private AnchorPane allUserTabAnchorPane;
    private ObservableList<User> usersObservableList;
    private WebSocketClient webSocketClient;
    private Node allUserTab;
    private User userData;
    private VBox chatBox;
    private List<Label> messages = new ArrayList<>();
    private List<Label> sysMessages = new ArrayList<>();
    private int index = 0;
    private int userIndex;

    // ===========================================================================================
    // CONTROLLER
    // ===========================================================================================

    public ClientViewSubController(User model, Parent view, ChatEditor editor) {
        this.model = model;
        this.view = view;
        this.editor = editor;
    }

    public void init() {
        // load all view references
        leaveChatButton = (Button) view.lookup("#LeaveChatButton");
        sendMsgButton = (Button) view.lookup("#SendMsgButton");
        chatBoxTabPane = (TabPane) view.lookup("#ChatBoxTabPane");
        allUserTab = view.lookup("#AllUserTab");
        inputTextField = (TextField) view.lookup("#InputTextField");
        chatListView = (ListView<User>) view.lookup("#ChatListView");
        chatListView.setCellFactory(new chatListViewCellFactory());
        allUserTabAnchorPane = (AnchorPane) view.lookup("#AllUserTabAnchorPane");
        chatBox = (VBox) view.lookup("#ChatBox");

        // set on mouse action
        leaveChatButton.setOnAction(this::leaveChatButtonOnClick);
        sendMsgButton.setOnAction(this::sendMsgButtonOnClick);
        inputTextField.setOnMouseClicked(this::inputTextFieldActivated);
        allUserTab.setOnMouseClicked(this::allUserTabActivated);
        chatListView.setOnMouseReleased(this::chatListViewOnDoubleClick);

        // ListView init
        usersObservableList = FXCollections.observableArrayList();
        // add to list
        usersObservableList.addAll(this.editor.getUserList());
        chatListView.setItems(usersObservableList);

        // REST LOGIN
        RestClient.login(this.model.getName(), response -> {
            JSONObject parse = response.getBody().getObject();
            String ret = parse.getString(COM_STATUS);
            URI uri = URI.create(CHAT_SOCKET_URL);
            // if status success (user have been created and flagged to "online")
            if (ret.equals("success")) {
                userIndex = 0;
                this.webSocketClient = new WebSocketClient(this.editor, uri, this::handleMessage, this.model);

                RestClient.getAllAvailableUser(response1 -> {
                    JSONObject parse1 = response1.getBody().getObject();
                    String string = parse1.getString(COM_DATA + COM_NAME);
                    usersObservableList.add(this.editor.getUser(string));
                    chatListView.refresh();
                });
            }
        });

        // PCL
        for (User user : this.editor.getUserList()
        ) {
            user.addPropertyChangeListener(user.PROPERTY_STATUS, this::onUserStatusChanged);
        }

    }

    private void onUserStatusChanged(PropertyChangeEvent propertyChangeEvent) {
        chatListView.refresh();
    }

    private void handleMessage(JsonStructure jsonStructure) {
        JsonObject parse = JsonUtil.parse(jsonStructure.toString());
        String message = parse.getString(COM_MSG);
        String channel = parse.getString(COM_CHANNEL);
        VBox userChatBox = new VBox();
        List<Label> userMessageList = new ArrayList<>();

        if (channel.equals(COM_CHANNEL_SYSTEM)) {
            System.out.println("Sys CHANNEL");
            sysMessages.add(new Label(message));
            // Platform run later for UI Refresh
            Platform.runLater(() -> {
                chatBox.getChildren().add(sysMessages.get(index));
                index++;
            });
        } else if (channel.equals(COM_CHANNEL_ALL)) {
            messages.add(new Label(message));
            // Platform run later for UI Refresh
            Platform.runLater(() -> {
                chatBox.getChildren().add(messages.get(index));
                index++;
            });
        } else if (channel.equals(COM_CHANNEL_PRIVATE)) {
            Platform.runLater(() -> {
                String userTo = parse.getString(COM_FROM);

                // create new tab if not exists
                ObservableList<Tab> tabList = chatBoxTabPane.getTabs();
                boolean found = false;
                for (Tab tab : tabList
                ) {
                    if (tab.getText().equals(userTo)) {
                        Label userLabel = new Label();
                        userLabel.setId(userTo + "Label");
                        userLabel.setText(message);
                        tab.setContent(userLabel);
                        found = true;
                    }
                }
                if (!found) {
                    Tab userTab = new Tab();
                    userTab.setId(userTo + "Tab");
                    userTab.setText(userTo);
                    // create ChatBox
                    userTab.setContent(userChatBox);
                    chatBoxTabPane.getTabs().add(userTab);
                    chatBoxTabPane.getSelectionModel().select(userTab);
                }
                userMessageList.add(new Label(message));
                userChatBox.getChildren().add(userMessageList.get(userIndex));
                userIndex++;
            });
        }
    }

    public void stop() {
        this.inputTextField.setOnAction(null);
        this.leaveChatButton.setOnAction(null);
        this.sendMsgButton.setOnAction(null);
        this.webSocketClient.stop();
    }

    // ===========================================================================================
    // BUTTON ACTIONS
    // ===========================================================================================

    private void chatListViewOnDoubleClick(MouseEvent event) {
        if (event.getClickCount() == 1) {
            userData = chatListView.getFocusModel().getFocusedItem();
        }
        if (event.getClickCount() == 2) {
            // create tab
            ObservableList<Tab> tabList = chatBoxTabPane.getTabs();
            boolean found = false;
            for (Tab tab : tabList
            ) {
                if (tab.getText().equals(userData.getName())) {
                    found = true;
                    chatBoxTabPane.getSelectionModel().select(tab);
                }
            }
            if (!found) {
                Tab userTab = new Tab();
                userTab.setId(userData.getName() + "Tab");
                userTab.setText(userData.getName());
                chatBoxTabPane.getTabs().add(userTab);
                chatBoxTabPane.getSelectionModel().select(userTab);
            }
        }
    }

    private void inputTextFieldActivated(MouseEvent event) {
        System.out.println("Input Textfield");
    }

    private void allUserTabActivated(MouseEvent event) {
        System.out.println("All User Tab activated");
    }

    private void sendMsgButtonOnClick(ActionEvent event) {
        // check if allTab
        if (chatBoxTabPane.getSelectionModel().isSelected(0)) {
            // build public message
            JsonObject allMessage = JsonUtil.buildPublicChatMessage(inputTextField.getText());
            this.webSocketClient.sendMessage(allMessage.toString());
        } else {
            // check if single User Tab
            String username = chatBoxTabPane.getSelectionModel().getSelectedItem().getText();
            // build private message
            JsonObject privateMessage = JsonUtil.buildPrivateChatMessage(inputTextField.getText(), username);
            webSocketClient.sendMessage(privateMessage.toString());
        }
        // clear input field
        this.inputTextField.clear();
    }

    public void leaveChatButtonOnClick(ActionEvent event) {
        RestClient.logout(this.model.getName(), response -> {
            JSONObject parse = response.getBody().getObject();
            String ret = parse.getString(COM_DATA + COM_MSG);
            if (ret.equals("bye")) {
                // successfull logoff
                System.out.println("test");
            }
        });
        stop();
        StageManager.showMiniChatStart();
    }

    // ===========================================================================================
    // SUBCONTROLLER INIT & HELPER
    // ===========================================================================================

}
