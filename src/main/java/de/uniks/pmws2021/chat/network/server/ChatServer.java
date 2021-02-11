package de.uniks.pmws2021.chat.network.server;

import de.uniks.pmws2021.chat.ChatEditor;
import de.uniks.pmws2021.chat.model.Chat;
import de.uniks.pmws2021.chat.model.User;
import de.uniks.pmws2021.chat.network.server.controller.UserController;
import de.uniks.pmws2021.chat.network.server.websocket.ChatSocket;

import java.io.EOFException;

import static de.uniks.pmws2021.chat.Constants.*;
import static spark.Spark.*;

public class ChatServer {
    // websocket
    private final ChatSocket chatSocket;
    private Chat model;
    private ChatEditor editor;
    // controller
    private UserController userController;

    public ChatServer(Chat model, ChatEditor editor) {

        // set port
        port(SERVER_PORT);

        // create chatsocket
        chatSocket = new ChatSocket(editor);

        // create user controller
        UserController userController = new UserController(model, editor, chatSocket);

        // start websocket
        webSocket(CHAT_WEBSOCKET_PATH, chatSocket);
        init();

        // rest setup
        before("*", ((request, response) -> response.header("Access-Control-Allow-Origin", "*")));
        before("*", ((request, response) -> response.header("Access-Control-Allow-Headers", "*")));
        before("*", ((request, response) -> response.header("Content-Type", "application/json")));

        // define routes with api prefix
        // define endpoints under user path

        path(API_PREFIX, () -> {
            path(USERS_PATH, () -> {
                get("", userController::getAllLoggedInUsers);
                post(LOGIN_PATH, userController::login);
                post(LOGOUT_PATH, userController::logout);
            });
        });

        // error handling
        notFound((request, response) -> "404 - User not found");
        internalServerError(((request, response) -> "Server tired, server sleeping"));

        // catch all exceptions from server
        exception(Exception.class, (e, req, res) -> {
            if (!(e instanceof EOFException)) {
                System.err.println("Error in chat server:");
                e.printStackTrace();
            }
        });
    }

    public void disconnectUser(User user) {
        // disconnect given user
        chatSocket.killConnection(user, "Disconnect");
        user.setStatus(false);
    }

    public void stopServer() {
        stop();
    }

}
