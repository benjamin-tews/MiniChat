package de.uniks.pmws2021.chat.network.client;

import javax.websocket.ClientEndpointConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static de.uniks.pmws2021.chat.Constants.COM_NAME;

public class CustomWebSocketConfigurator extends ClientEndpointConfig.Configurator {
    private final String name;

    public CustomWebSocketConfigurator(String name) {
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
