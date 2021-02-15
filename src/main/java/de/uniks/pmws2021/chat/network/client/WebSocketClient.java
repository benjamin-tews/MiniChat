package de.uniks.pmws2021.chat.network.client;

import de.uniks.pmws2021.chat.ChatEditor;

import javax.websocket.*;
import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;

public class WebSocketClient extends Endpoint {

    private Session session;
    private Timer noopTimer;

    private final ChatEditor model;

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
                // Send NOOP Message
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
        // Use callback to handle it
    }

    public void sendMessage(String message) {
        // check if session is still open
        // send message
    }

    public void stop() {
        // cancel timer
        // close session
    }
}
