package de.uniks.pmws2021.chat;

import de.uniks.pmws2021.chat.model.Chat;
import de.uniks.pmws2021.chat.network.server.ChatServer;
import de.uniks.pmws2021.chat.network.server.websocket.ChatSocket;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.Assert;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.TAB;

public class StageManagerTest extends ApplicationTest {

    private Stage stage;
    private StageManager app;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        app = new StageManager();
        app.start(stage);
        this.stage.centerOnScreen();
    }

    @Test
    public void showMiniChatStart() {

        // CHAT START VIEW
        Assert.assertEquals("PMWS2021 - Mini Chat", stage.getTitle());

            // init buttons
            Button serverButton = lookup("#ServerButton").query();

            // check buttons
            clickOn(serverButton);

        // SERVER SUB VIEW
        Assert.assertEquals("PMWS2021 - Mini Chat::Server", stage.getTitle());

            // init buttons
            Button disconnectAllButton = lookup("#DisconnectAllButton").query();
            Button disconnectOneButton = lookup("#DisconnectOneButton").query();
            Button closeServerButton = lookup("#CloseServerButton").query();

            // check buttons
            clickOn(disconnectAllButton);
            clickOn(disconnectOneButton);
            clickOn(closeServerButton);

        // CHAT START VIEW
        Assert.assertEquals("PMWS2021 - Mini Chat", stage.getTitle());

            // init buttons
            Button clientButton = lookup("#ClientButton").query();

            // check buttons
            clickOn(clientButton);

            // input dialog
            write("Benjamin");
            type(TAB);
            type(ENTER);


        // CLIENT SUB VIEW
        Assert.assertEquals("PMWS2021 - Mini Chat::Client", stage.getTitle());

            // init buttons
            Button leaveChatButton = lookup("#LeaveChatButton").query();
            Button sendMsgButton = lookup("#SendMsgButton").query();
            TextField inputTextField = lookup("#InputTextField").query();

            // check buttons
            clickOn(inputTextField);
            write("Hello Dude - na wie gehts?");
            sleep(1000);
            clickOn(sendMsgButton);
            Assert.assertTrue(inputTextField.getText().isEmpty());
            clickOn(leaveChatButton);

        // CHAT START VIEW
        Assert.assertEquals("PMWS2021 - Mini Chat", stage.getTitle());

        // DONE

    }

}