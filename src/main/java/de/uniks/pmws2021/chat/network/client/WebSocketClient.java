package de.uniks.pmws2021.chat.network.client;

import de.uniks.pmws2021.chat.ChatEditor;
import de.uniks.pmws2021.chat.util.JsonUtil;

import javax.json.JsonObject;
import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;

public class WebSocketClient extends Endpoint {

    private final ChatEditor model;
    private Session session;
    private Timer noopTimer;
    private WSCallback callback;

    public WebSocketClient(ChatEditor model, URI endpoint, WSCallback callback) {
        this.model = model;
        this.noopTimer = new Timer();

        try {
            ClientEndpointConfig clientConfig = ClientEndpointConfig.Builder.create()
                    .configurator(new CustomWebSocketConfigurator(endpoint.getAuthority()))
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
                // ToDo Send NOOP Message
            }
        }, 0, 1000 * 30);
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        super.onClose(session, closeReason);
        // cancel timer
        // set session null
    }

    private void onMessage(String message) {
        // Process Message
        JsonObject parse = JsonUtil.parse(message);
        // Use callback to handle it
        this.callback.handleMessage(parse);
    }

    public void sendMessage(String message) {
        // check if session is still open
        if (this.session.isOpen()) {
            // RESPONSE msg to server
            this.session.getAsyncRemote().sendText(message);
        }
    }

    public void stop() {
        // cancel timer
        // close session
    }

}
