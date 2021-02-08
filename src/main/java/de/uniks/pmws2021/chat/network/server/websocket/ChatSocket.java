package de.uniks.pmws2021.chat.network.server.websocket;

import de.uniks.pmws2021.chat.ChatEditor;
import de.uniks.pmws2021.chat.Constants;
import de.uniks.pmws2021.chat.model.User;
import de.uniks.pmws2021.chat.util.ServerResponse;
import de.uniks.pmws2021.chat.util.JsonUtil;
import de.uniks.pmws2021.chat.util.ValidationUtil;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;

import javax.json.JsonObject;
import javax.json.JsonValue;
import java.io.EOFException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

import static de.uniks.pmws2021.chat.Constants.*;

@WebSocket
public class ChatSocket {
    private final ChatEditor editor;

    // save all connections to send messages correctly
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
        String name = session.getUpgradeRequest().getHeader(COM_NAME);
        User user = this.editor.getUser(name);

        if (name != null && !name.isEmpty()) {
            // check if user has logged in
            if (user.getStatus()) {
                // if yes, store user with his session and add session to clients
                userSessionMap.put(name, session);
                clients.add(session);
                // also send a system message that the user has logged in
                sendSystemMessage(JsonUtil.buildUserJoinedSystemMessage(user).toString());
            } else {
                // if not, send response that the user has to log in first and close session
                session.getRemote().sendString(JsonUtil.stringify(new ServerResponse(ServerResponse.FAILURE, "User has to login first")));
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
        // just some logging
        System.out.println("Chat session closed, because of " + reason);

        // if user is logged in remove session and send system message to notify all about logout

        if (userSessionMap.containsKey(session)) {
            session.close();
            userSessionMap.remove(session);
            // ToDo: Build message ...
            for (Session blah : clients
            ) {
                try {
                    blah.getRemote().sendString(JsonUtil.buildPublicChatMessage("Session: " + session.toString() + " closed").toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                session.getRemote().sendString(JsonUtil.buildPublicChatMessage("Session: " + session.toString() + " closed").toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
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

            // get user name (from)
            String userName = session.getUpgradeRequest().getHeader(COM_FROM);

            /*
            for(Map.Entry<String, Session> entry : userSessionMap.entrySet()){
                if( entry.getValue().equals(session) ){
                    userName = entry.getKey();
                }
            }*/

            // parse string message to json
            JsonObject parse = JsonUtil.parse(message);

            // get channel identifier
            String channel = parse.getString(COM_CHANNEL);

            // build answer message (channel, from, message)
            // ToDO: something to be done?


            // Check if the message is public or private
            // if message is public, send message to every client
            // check if session is open before sending message
            if (channel.equals(COM_CHANNEL_ALL)) {
                // public message
                if (session.isOpen()) {
                    sendSystemMessage(message);
                }
            } else {
                // if message is private
                // lookup session of receiving user
                String receivingUser = parse.getString(COM_TO);
                // check if session is open
                if (session.isOpen()) {
                    // send message to the receiver
                    session.getRemote().sendString(JsonUtil.buildPrivateChatMessage(message, receivingUser).toString());
                }
            }

        } catch (Exception e) {
            System.err.println("Error while processing incoming message:");
            e.printStackTrace();
        }
    }

    public void sendUserJoined(User user) {
        // send system message with data
        JsonUtil.buildUserJoinedSystemMessage(user);
    }

    public void sendUserLeft(User user) {
        // send system message with data
        JsonUtil.buildUserLeftSystemMessage(user);
    }

    public void killConnection(User user, String reason) {
        // get session of user, remove from lists and close it (if open)
        if (userSessionMap.containsKey(user.getName())) {
            Session userSession = userSessionMap.get(user.getName());
            userSessionMap.remove(userSession);
            if (userSession.isOpen()) {
                userSession.close();
            }
        }
        sendSystemMessage(user + "s' Session killed: " + reason);
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
