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

public class SendWebSocketMessageTest {

    private static final String ALBERT_USERNAME = "Albert";
    private static final String SENT_MESSAGE = "Hello Testing World";
    private static ClientEndpointConfig CLIENT_CONFIG = null;
    private static ClientTestEndpoint CLIENT = null;
    private static URI Chat_URL;
    private CountDownLatch messageLatch;


    private void setupWebsocketClient(int count) throws URISyntaxException {
        System.out.println("Starting WebSocket Client");
        CLIENT_CONFIG = ClientEndpointConfig.Builder.create()
                .configurator(new TestWebSocketConfigurator(ALBERT_USERNAME))
                .build();
        CLIENT = new ClientTestEndpoint();
        messageLatch = new CountDownLatch(count);
        Chat_URL = new URI(CHAT_SOCKET_URL);
    }

    private void shutDownWebSocketClient() throws IOException {
        System.out.println("Closing WebSocket Client\n");
        CLIENT.getSession().close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "Test was finished"));
    }

    @Test
    public void testCreateWebsocketConnection() throws DeploymentException, IOException, InterruptedException, URISyntaxException {
        setupWebsocketClient(1);
        connectWebSocketClient();
        boolean messageReceivedByClient = messageLatch.await(10, TimeUnit.SECONDS);
        Assert.assertTrue("Time lapsed before message was received by client.", messageReceivedByClient);
        shutDownWebSocketClient();
    }

    @Test
    public void testSendAllMessage() throws DeploymentException, IOException, InterruptedException, URISyntaxException {
        setupWebsocketClient(2);
        connectWebSocketClient();
        CLIENT.sendMessage(JsonUtil.buildPublicChatMessage(SENT_MESSAGE).toString());
        boolean messageReceivedByClient = messageLatch.await(20, TimeUnit.SECONDS);
        Assert.assertTrue("Time lapsed before message was received by client.", messageReceivedByClient);
        shutDownWebSocketClient();
    }

    private void connectWebSocketClient() throws DeploymentException, IOException {
        try {
            ContainerProvider.getWebSocketContainer().connectToServer(CLIENT, CLIENT_CONFIG, Chat_URL);
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
            messageLatch.countDown(); // Count incoming messages
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