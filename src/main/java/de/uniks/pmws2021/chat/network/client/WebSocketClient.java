package de.uniks.pmws2021.chat.network.client;

import de.uniks.pmws2021.chat.ChatEditor;
import de.uniks.pmws2021.chat.model.User;
import de.uniks.pmws2021.chat.util.JsonUtil;
import kong.unirest.Unirest;

import javax.json.JsonObject;
import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;

import static de.uniks.pmws2021.chat.Constants.COM_NOOP;

public class WebSocketClient extends Endpoint {

    private final ChatEditor model;
    private User user;
    private Session session;
    private Timer noopTimer;
    private WSCallback callback;

    public WebSocketClient(ChatEditor model, URI endpoint, WSCallback callback, User user) {
        this.model = model;
        this.user = user;
        this.noopTimer = new Timer();

        try {
            ClientEndpointConfig clientConfig = ClientEndpointConfig.Builder.create()
                    .configurator(new CustomWebSocketConfigurator(user.getName()))
                    .build();

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(
                    this,
                    clientConfig,
                    endpoint
            );
            this.callback = callback;
        } catch (Exception e) {
            System.err.println("Error during establishing websocket connection:");
            e.printStackTrace();
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        // Store session
        this.session = session;

        // add MessageHandler
        this.session.addMessageHandler(String.class, this::onMessage);

        this.noopTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    session.getBasicRemote().sendText(COM_NOOP);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 1000 * 30);
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        super.onClose(session, closeReason);
        // cancel timer
        this.noopTimer.cancel();
        // set session null
        this.session = null;
    }

    private void onMessage(String message) {
        // Process Message
        JsonObject parse = JsonUtil.parse(message);
        // Use callback to handle it
        this.callback.handleMessage(parse);
    }

    public void sendMessage(String message) {
        // check if session is still open
        if (session.isOpen()) {
            // RESPONSE msg to server
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        // cancel timer
        this.noopTimer.cancel();
        // close session
        Unirest.shutDown();
    }

}
