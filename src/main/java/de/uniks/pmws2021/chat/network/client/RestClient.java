package de.uniks.pmws2021.chat.network.client;

import de.uniks.pmws2021.chat.ChatEditor;
import de.uniks.pmws2021.chat.Constants;
import de.uniks.pmws2021.chat.model.User;
import de.uniks.pmws2021.chat.util.JsonUtil;
import de.uniks.pmws2021.chat.util.ServerResponse;
import kong.unirest.Callback;
import kong.unirest.HttpRequest;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import org.json.JSONObject;

import javax.json.Json;
import javax.json.JsonObject;

import java.util.Arrays;

import static de.uniks.pmws2021.chat.Constants.*;


public class RestClient {
    private static ChatEditor chatEditor;

    // User actions
    public static void login(String name, Callback<JsonNode> callback) {
        // Build Request Body
        JSONObject reqBody = new JSONObject();
        reqBody.put(COM_NAME, name);
        // Use UniRest to make login request, use the right request method and attach body data
        HttpRequest<?> req = Unirest.post(REST_SERVER_URL+API_PREFIX+USERS_PATH+LOGIN_PATH)
                .header(COM_NAME, name)
                .body(reqBody);
        sendRequest(req, callback);
    }
    
    public static void logout(String name, Callback<JsonNode> callback) {
        // Build Request Body
       // JsonObject reqBody = Json.createObjectBuilder().add(COM_NAME, name).build();
        // Use UniRest to make logout request, use the right request method and attach body data
        //HttpRequest<?> req;
        HttpRequest<?> req = Unirest.post(REST_SERVER_URL+API_PREFIX+USERS_PATH+LOGOUT_PATH)
                .header("reqBody", name);
        sendRequest(req, callback);
    }

    public static void getAllAvailableUser(Callback<JsonNode> callback) {
        // Use UniRest to make get user request
        HttpRequest<?> req = Unirest.get(REST_SERVER_URL+API_PREFIX+USERS_PATH);
        sendRequest(req, callback);
    }

    private static void sendRequest(HttpRequest<?> req, Callback<JsonNode> callback) {
        new Thread(() -> req.asJsonAsync(callback)).start();
    }

}
