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
import java.io.EOFException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

import static de.uniks.pmws2021.chat.Constants.COM_CHANNEL_ALL;
import static de.uniks.pmws2021.chat.Constants.COM_NAME;

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
        String name = " ";
        name = session.getUpgradeRequest().getHeader(COM_NAME);

        if (name != null && !name.isEmpty()) {
            // check if user has logged in
            if (this.editor.haveUser(name, "127.0.0.1").getStatus()) {
                // if yes, store user with his session and add session to clients
                userSessionMap.put(name, session);
                clients.add(session);
                // also send a system message that the user has logged in
                sendUserJoined(this.editor.haveUser(name, "127.0.0.1"));
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
            // ToDo: Build better message ...
            //sendSystemMessage(session.toString() + " logged out");
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
            String userName = " ";

            for(Map.Entry<String, Session> entry : userSessionMap.entrySet()){
                if( entry.getValue().equals(session) ){
                    userName = entry.getKey();
                }
            }

            // get user name (to) ??

            // parse string message to json
            JsonObject parse = JsonUtil.parse(message);

            // get channel identifier
            String channel = COM_CHANNEL_ALL;

            // build answer message (channel, from, message)

            // Check if the message is public or private
            if (channel.equals(COM_CHANNEL_ALL) ) {
                // public message
                if (session.isOpen()) {
                    JsonUtil.buildPublicChatMessage(message);
                }
            } else {
                // private message
                if (session.isOpen()) {
                    JsonUtil.buildPrivateChatMessage(message, userName);
                }
            }

            // if message is public, send message to every client
            // check if session is open before sending message

            // if message is private
            // lookup session of receiving user
            // check if session is open
            // send message to the receiver
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
        JsonUtil.buildPublicChatMessage(message);
    }

}
