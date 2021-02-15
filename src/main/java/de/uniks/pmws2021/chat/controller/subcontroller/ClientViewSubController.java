package de.uniks.pmws2021.chat.controller.subcontroller;

import de.uniks.pmws2021.chat.ChatEditor;
import de.uniks.pmws2021.chat.StageManager;
import de.uniks.pmws2021.chat.model.User;
import de.uniks.pmws2021.chat.network.client.RestClient;
import de.uniks.pmws2021.chat.network.client.WebSocketClient;
import de.uniks.pmws2021.chat.util.JsonUtil;
import de.uniks.pmws2021.chat.util.ResourceManager;
import de.uniks.pmws2021.chat.view.chatListViewCellFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import kong.unirest.JsonNode;
import org.json.JSONObject;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonStructure;
import javax.json.JsonValue;

import java.net.URI;
import java.util.List;

import static de.uniks.pmws2021.chat.Constants.*;

public class ClientViewSubController {

    private User model;
    private Parent view;
    private ChatEditor editor;
    private Button leaveChatButton;
    private Button sendMsgButton;
    private Node allUserTab;
    private TabPane chatBoxTabPane;
    private TextField inputTextField;
    private ListView<User> chatListView;
    private AnchorPane allUserTabAnchorPane;
    private ObservableList<User> usersObservableList;

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

        // set on mouse action
        leaveChatButton.setOnAction(this::leaveChatButtonOnClick);
        sendMsgButton.setOnAction(this::sendMsgButtonOnClick);
        inputTextField.setOnMouseClicked(this::inputTextFieldActivated);
        allUserTab.setOnMouseClicked(this::allUserTabActivated);
        chatListView.setOnMouseReleased(this::chatListViewOnDoubleClick);

        // ListView Init
        usersObservableList = FXCollections.observableArrayList();
        // add to list
        usersObservableList.addAll(this.editor.getUserList());

        // ToDo Übertrag

        // REST LOGIN
        RestClient.login(model.getName(), response -> {
            JSONObject parse = response.getBody().getObject();
            String ret = parse.get(COM_STATUS).toString();
            // if status success (user have been created and flagged to "online")
            // Debugging
            System.out.println("ret1:" + ret);
            if (ret.equals("success")) {
                new WebSocketClient(this.editor, URI.create(WS_SERVER_URL+CHAT_WEBSOCKET_PATH), this::handleMessage);
            } else {
                System.out.println("ret2:" + ret);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Message Here...");
                alert.setHeaderText("Look, an Information Dialog");
                alert.setContentText("I have a great message for you!");
                alert.showAndWait().ifPresent(rs -> {
                    if (rs == ButtonType.OK) {
                        System.out.println("Pressed OK.");
                    }
                });
            }
            // else message box error
            // TODO ÜBERTRAG
        });

        // logon

        // get all User online and
        RestClient.getAllAvailableUser(response -> {
           /* JSONObject getBody = response.getBody().getObject();
            JSONObject parse = (JSONObject) getBody.get(COM_DATA);
            List<User> userList = JsonUtil.parseUsers((JsonArray) parse.get(COM_NAME));
            usersObservableList.addAll(userList);*/
            JSONObject getBody = response.getBody().getObject();
            String userName = getBody.get(COM_NAME).toString();
            System.out.println(userName);
            // ToDo set userList from response
            usersObservableList.addAll(this.editor.getUserList());
        });


        // ToDo: unsafe operation - fix this
        chatListView.setItems(usersObservableList);
        chatListView.refresh();

        // PCL
        // ToDo add PCL if user joins/leaves

    }

    private void handleMessage(JsonStructure jsonStructure) {
        // if message is private or public
    }

    public void stop() {
        inputTextField.setOnAction(null);
        leaveChatButton.setOnAction(null);
        sendMsgButton.setOnAction(null);
    }

    // ===========================================================================================
    // BUTTON ACTIONS
    // ===========================================================================================

    private void chatListViewOnDoubleClick(MouseEvent event) {
        if (event.getClickCount() == 2) {
            System.out.println("Doubleclick on List Item");
            // ToDo open new Tab / Webconenction
        }
    }

    private void inputTextFieldActivated(MouseEvent event) {
        System.out.println("Input Textfield");
    }

    private void allUserTabActivated(MouseEvent event) {
        System.out.println("All User Tab activated");
    }

    private void sendMsgButtonOnClick(ActionEvent event) {
        System.out.println(inputTextField.getText());
        inputTextField.clear();
    }

    private void leaveChatButtonOnClick(ActionEvent event) {
        System.out.println("Disconnect ClientView");
        StageManager.showMiniChatStart();
    }

    // ===========================================================================================
    // SUBCONTROLLER INIT & HELPER
    // ===========================================================================================

}
