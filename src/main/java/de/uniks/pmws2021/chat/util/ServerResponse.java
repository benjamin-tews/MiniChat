package de.uniks.pmws2021.chat.util;

import javax.json.JsonStructure;
import javax.json.JsonValue;

public class ServerResponse {
    public static final String SUCCESS = "success";
    public static final String FAILURE = "failure";

    private final String status;
    private final String message;
    private final JsonStructure jsonData;

    public ServerResponse() {
        this.status = FAILURE;
        this.message = "Something went terrible wrong";
        this.jsonData = JsonValue.EMPTY_JSON_OBJECT;
    }

    public ServerResponse(String status, String message) {
        this.status = status;
        this.message = message;
        this.jsonData = JsonValue.EMPTY_JSON_OBJECT;
    }

    public ServerResponse(String status, JsonStructure jsonData) {
        this.status = status;
        this.message = "";
        this.jsonData = jsonData;
    }

    public ServerResponse(String status, String message, JsonStructure jsonData) {
        this.status = status;
        this.message = message;
        this.jsonData = jsonData;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public JsonStructure getJsonData() {
        return jsonData;
    }
}
