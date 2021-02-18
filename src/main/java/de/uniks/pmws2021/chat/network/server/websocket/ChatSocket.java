package de.uniks.pmws2021.chat.network.server.websocket;

import de.uniks.pmws2021.chat.ChatEditor;
import de.uniks.pmws2021.chat.Constants;
import de.uniks.pmws2021.chat.model.User;
import de.uniks.pmws2021.chat.util.JsonUtil;
import de.uniks.pmws2021.chat.util.ServerResponse;
import de.uniks.pmws2021.chat.util.ValidationUtil;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;

import javax.json.JsonObject;
import javax.websocket.server.PathParam;
import java.io.EOFException;
import java.io.IOException;
import java.security.Principal;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

import static de.uniks.pmws2021.chat.Constants.*;

@WebSocket
public class ChatSocket {

    private final ChatEditor editor;
    private final Map<String, Session> userSessionMap;
    private final Queue<Session> clients;

    public ChatSocket(ChatEditor editor) {
        this.editor = editor;
        this.userSessionMap = new LinkedHashMap<>();
        this.clients = new ConcurrentLinkedDeque<>();
    }

    @OnWebSocketConnect
    public void onNewConnection(Session session) throws IOException {
        // get user name from session request header
        String name = " ";
        name = session.getUpgradeRequest().getHeader(COM_NAME);
        System.out.println("Debug Username: " + name);

        if (name != null && !name.isEmpty()) {
            // check if user has logged in
            if (this.editor.haveUser(name).getStatus()) {
                // if yes, store user with his session and add session to clients
                userSessionMap.put(name, session);
                clients.add(session);
                // also send a system message that the user has logged in
                sendUserJoined(this.editor.getUser(name));
            } else {
                // if not, send response that the user has to log in first and close session
                session.getRemote().sendString(JsonUtil.stringify(new ServerResponse(ServerResponse.FAILURE, "User has to login first")));
                session.close(1000, "user not logged in");
            }
        } else {
            // send message to given session
            session.getRemote().sendString(JsonUtil.stringify(new ServerResponse(ServerResponse.FAILURE, "Missing name parameter in header")));
            session.getRemote().flush();
            // close session
            session.close(1000, "Incorrect connection.");
        }
    }

    @OnWebSocketClose
    public void onConnectionClose(Session session, int statusCode, String reason) {

        // if user is logged in remove session and send system message to notify all about logout
        if (userSessionMap.containsKey(session)) {

            String userName = session.getUpgradeRequest().getHeader(COM_FROM);
            System.out.println("Debug username: " + userName);

            for (Map.Entry<String, Session> entry : userSessionMap.entrySet()) {
                // if session in userSessionMap and if its open
                if (entry.getValue().equals(session) && entry.getValue().isOpen()) {
                    killConnection(this.editor.haveUser((entry.getKey())), reason);
                    userSessionMap.remove(session);
                    clients.remove(session);
                    sendUserLeft(this.editor.getUser(userName));
                }
            }
        } else {
            System.err.println("User not connected");
        }
    }

    @OnWebSocketError
    public void onSocketError(Throwable e) {
        // Only print errors which belong to a real failure
        if (!(e instanceof EOFException)) {
            System.err.println("Error on chat socket:");
            e.printStackTrace();
        }
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        try {
            // If this was a noop, just do nothing
            if (message.equals(Constants.COM_NOOP)) {
                return;
            }

            // Validate the chat message
            ServerResponse err = ValidationUtil.validateChatMessage(message);
            if (err != null) {
                try {
                    // send message to given session
                    session.getRemote().sendString(JsonUtil.stringify(err));
                    session.getRemote().flush();
                    return;
                } catch (Exception e) {
                    System.err.println("Error while processing incoming message:");
                    e.printStackTrace();
                }
            }

            // parse string message to json
            JsonObject parseMessage = JsonUtil.parse(message);

            // get channel identifier
            String channel = parseMessage.getString(COM_CHANNEL);
            String msg = parseMessage.getString(COM_MSG);


            // Check if the message is public or private
            // if message is public, send message to every client
            // check if session is open before sending message
            if (channel.equals(COM_CHANNEL_ALL)) {
                // public message
                System.out.println("message to all");
                if (session.isOpen()) {
                    System.out.println("message to all 2");
                    session.getRemote().sendString(JsonUtil.buildPublicChatMessageServer(msg).toString());
                }
            } else if (channel.equals(COM_CHANNEL_PRIVATE)) {
                // if message is private
                // lookup session of receiving user
                System.out.println("private");
                String receivingUser = parseMessage.getString(COM_TO);
                System.out.println("Username parse from header: " + receivingUser);

                // send message to the receiver
                session.getRemote().sendString(JsonUtil.buildPrivateChatMessageServer(msg, receivingUser).toString());
            }

        } catch (Exception e) {
            System.err.println("Error while processing incoming message:");
            e.printStackTrace();
        }
    }

    public void sendUserJoined(User user) {
        // ToDo: use sendSystemMessage()
        sendSystemMessage(JsonUtil.buildUserJoinedSystemMessage(user).toString());
    }


    public void sendUserLeft(User user) {
        // ToDo: use sendSystemMessage()
        for (Session session : clients
        ) {
            try {
                session.getRemote().sendString(JsonUtil.buildUserLeftSystemMessage(user).toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void killConnection(User user, String reason) {
        // get session of user, remove from lists and close it (if open)

        if (userSessionMap.containsKey(user.getName())) {
            Session userSession = userSessionMap.get(user.getName());
            userSessionMap.remove(userSession);
            if (userSession.isOpen()) {
                userSession.close(); // calls implicit onConnectionClose
            }
        }
    }

    private void sendSystemMessage(String message) {
        // send message to every client
        for (Session session : clients
        ) {
            try {
                session.getRemote().sendString(JsonUtil.buildPublicChatMessage(message).toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
