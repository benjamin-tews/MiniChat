package de.uniks.pmws2021.chat.controller.subcontroller;

import de.uniks.pmws2021.chat.ChatEditor;
import de.uniks.pmws2021.chat.StageManager;
import de.uniks.pmws2021.chat.model.User;
import de.uniks.pmws2021.chat.network.client.RestClient;
import de.uniks.pmws2021.chat.network.client.WebSocketClient;
import de.uniks.pmws2021.chat.util.JsonUtil;
import de.uniks.pmws2021.chat.view.chatListViewCellFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.json.JSONObject;

import javax.json.JsonObject;
import javax.json.JsonStructure;
import java.net.URI;

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
    private Label testLabel;

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
        testLabel = (Label) view.lookup("#TestLabel");

        // ToDo: TabPane Elements - just dummies for now


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

        // REST LOGIN
        RestClient.login(this.model.getName(), response -> {
            JSONObject parse = response.getBody().getObject();
            String ret = parse.getString(COM_STATUS);
            // if status success (user have been created and flagged to "online")
            //if (ret.equals("success")) {
                System.out.println("success: " + ret);
                this.webSocketClient = new WebSocketClient(this.editor, URI.create(WS_SERVER_URL + CHAT_WEBSOCKET_PATH), this::handleMessage);
                System.out.println("ret2:" + ret);
            //}
        });

        // ToDo: unsafe operation - fix this
        chatListView.setItems(usersObservableList);
        chatListView.refresh();

        // PCL
        // ToDo add PCL if user joins/leaves

    }

    private void handleMessage(JsonStructure jsonStructure) {
        System.out.println("we're in here handler" + jsonStructure.toString());
    }


    public void stop() {
        this.inputTextField.setOnAction(null);
        this.leaveChatButton.setOnAction(null);
        this.sendMsgButton.setOnAction(null);
    }

    // ===========================================================================================
    // BUTTON ACTIONS
    // ===========================================================================================

    private void chatListViewOnDoubleClick(MouseEvent event) {
        if (event.getClickCount() == 2) {
            System.out.println("Doubleclick on List Item");
            // ToDo open new Tab / Webconnection
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

        // build public message
        JsonObject allMessage = JsonUtil.buildPublicChatMessage(inputTextField.getText());
        this.webSocketClient.sendMessage(allMessage.toString());
        // check if single User Tab

        // build private message
        // ToDo: create Tab for user from List and fill in username from list selection
        //JsonObject privateMessage = JsonUtil.buildPrivateChatMessage(inputTextField.getText(), "Anton");
        //this.webSocketClient.sendMessage(privateMessage.toString());

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
