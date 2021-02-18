package de.uniks.pmws2021.chat.network.client;

import javax.json.JsonStructure;

public interface WSCallback {
    void handleMessage(JsonStructure msg);
}
