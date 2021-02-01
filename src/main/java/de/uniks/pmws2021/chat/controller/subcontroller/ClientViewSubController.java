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
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ClientViewSubController {

    private Parent view;
    private ChatEditor editor;
    private Stage stage;
    private Button leaveChatButton;
    private Button sendMsgButton;
    private Node allUserTab;
    private TabPane chatBoxTabPane;
    private TextField inputTextField;
    private ListView<User> chatListView;
    private AnchorPane allUserTabAnchorPane;

    public ClientViewSubController(Parent view, ChatEditor editor, Stage stage) {
        this.view = view;
        this.editor = editor;
        this.stage = stage;
    }


    public void init() {
        // set view
        try {
            view = FXMLLoader.load(StageManager.class.getResource("view/MiniChatClient.fxml"));
            Scene scene = new Scene(view);

            // display via StageManager
            stage.setTitle("PMWS2021 - Mini Chat::Client");
            stage.setScene(scene);

            // load all view references
            leaveChatButton = (Button) view.lookup("#LeaveChatButton");
            sendMsgButton = (Button) view.lookup("#SendMsgButton");
            chatBoxTabPane = (TabPane) view.lookup("#ChatBoxTabPane");
            allUserTab = view.lookup("#AllUserTab");
            inputTextField = (TextField) view.lookup("#InputTextField");
            chatListView = (ListView<User>) view.lookup("#ChatListView");
            allUserTabAnchorPane = (AnchorPane) view.lookup("#AllUserTabAnchorPane");

            // set on mouse action
            leaveChatButton.setOnAction(this::leaveChatButtonOnClick);
            sendMsgButton.setOnAction(this::sendMsgButtonOnClick);
            inputTextField.setOnMouseClicked(this::inputTextFieldActivated);
            allUserTab.setOnMouseClicked(this::allUserTabActivated);

        } catch (Exception e) {
            System.err.println("Failed to load Chat Client :: ClientViewSubController init()");
            e.printStackTrace();
        }

    }

    private void inputTextFieldActivated(MouseEvent event) {
        System.out.println("Input Textfield");
    }

    private void allUserTabActivated(MouseEvent event) {
        System.out.println("All User Tab activated");
    }

    private void sendMsgButtonOnClick(ActionEvent event) {
        System.out.println("Send Message");
    }

    private void leaveChatButtonOnClick(ActionEvent event) {
        System.out.println("Disconnect ClientView");
        StageManager.showMiniChatStart();
    }

}
