import de.uniks.pmws2021.chat.util.JsonUtil;
import org.junit.Assert;
import org.junit.Test;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static de.uniks.pmws2021.chat.Constants.CHAT_SOCKET_URL;
import static de.uniks.pmws2021.chat.Constants.COM_NAME;

public class SendPrivateWebSocketMessageTest {

    private static final String ALBERT_USERNAME = "Albert";
    private static final String CLEMENS_USERNAME = "Clemens";
    private static final String SENT_MESSAGE = "Hello Albert";
    private static ClientEndpointConfig ALBERT_CLIENT_CONFIG = null;
    private static ClientEndpointConfig CLEMENS_CLIENT_CONFIG = null;
    private static ClientTestEndpoint ALBERT_CLIENT = null;
    private static ClientTestEndpoint CLEMENS_CLIENT = null;
    private static URI Chat_URL;
    private CountDownLatch messageLatch;


    private void setupWebsocketClient() throws URISyntaxException {
        System.out.println("Starting WebSocket Client");
        ALBERT_CLIENT_CONFIG = ClientEndpointConfig.Builder.create()
                .configurator(new TestWebSocketConfigurator(ALBERT_USERNAME))
                .build();
        CLEMENS_CLIENT_CONFIG = ClientEndpointConfig.Builder.create()
                .configurator(new TestWebSocketConfigurator(CLEMENS_USERNAME))
                .build();
        messageLatch = new CountDownLatch(1);
        ALBERT_CLIENT = new ClientTestEndpoint();
        CLEMENS_CLIENT = new ClientTestEndpoint();
        Chat_URL = new URI(CHAT_SOCKET_URL);
    }

    private void shutDownWebSocketClient() throws IOException {
        System.out.println("Closing WebSocket Client\n");
        ALBERT_CLIENT.getSession().close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "Test was finished"));
        CLEMENS_CLIENT.getSession().close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "Test was finished"));
    }

    @Test
    public void testSendAllMessage() throws DeploymentException, IOException, InterruptedException, URISyntaxException {
        setupWebsocketClient();
        connectWebSocketClient();

        CLEMENS_CLIENT.sendMessage(JsonUtil.buildPrivateChatMessage(SENT_MESSAGE, ALBERT_USERNAME).toString());
        boolean messageReceivedByClient = messageLatch.await(20, TimeUnit.SECONDS);
        Assert.assertTrue("Time lapsed before message was received by client.", messageReceivedByClient);
        shutDownWebSocketClient();
    }

    private void connectWebSocketClient() throws DeploymentException, IOException {
        try {
            ContainerProvider.getWebSocketContainer().connectToServer(ALBERT_CLIENT, ALBERT_CLIENT_CONFIG, Chat_URL);
            ContainerProvider.getWebSocketContainer().connectToServer(CLEMENS_CLIENT, CLEMENS_CLIENT_CONFIG, Chat_URL);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    class ClientTestEndpoint extends Endpoint {

        private Session session;

        public Session getSession() {
            return session;
        }

        @Override
        public void onOpen(Session session, EndpointConfig config) {
            this.session = session;
            this.session.addMessageHandler(String.class, this::onMessage);
        }

        public void sendMessage(String message) throws IOException {
            if (this.session != null && this.session.isOpen()) {
                this.session.getBasicRemote().sendText(message);
                this.session.getBasicRemote().flushBatch();
            }
        }

        private void onMessage(String message) {
            System.out.println("TEST CLIENT Received message: " + message);
            if (message.contains(SENT_MESSAGE)) {
                messageLatch.countDown(); // Count incoming messages
            }
        }
    }

    class TestWebSocketConfigurator extends ClientEndpointConfig.Configurator {
        private final String name;

        public TestWebSocketConfigurator(String name) {
            this.name = name;
        }

        @Override
        public void beforeRequest(Map<String, List<String>> headers) {
            super.beforeRequest(headers);
            ArrayList<String> key = new ArrayList<>();
            key.add(this.name);
            headers.put(COM_NAME, key);
        }
    }
}