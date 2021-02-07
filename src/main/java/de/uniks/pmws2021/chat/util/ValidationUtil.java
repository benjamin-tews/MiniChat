package de.uniks.pmws2021.chat.util;

import de.uniks.pmws2021.chat.Constants;

import javax.json.JsonObject;

/**
 * Validation class for checking the json communication, generates error message on wrong calls
 */
public class ValidationUtil {
    public static ServerResponse validateBody(String body) {
        if (body == null || body.isEmpty()) {
            return new ServerResponse(ServerResponse.FAILURE, "Missing body parameter: { \"name\": \"clemens\" }");
        }

        JsonObject json;
        try {
            json = JsonUtil.parse(body);
        } catch (Exception e) {
            return new ServerResponse(ServerResponse.FAILURE, e.getMessage());
        }
        String name;

        try {
            name = json.getString(Constants.COM_NAME);
        } catch (Exception e) {
            return new ServerResponse(ServerResponse.FAILURE, "Missing body parameter: { \"name\": \"clemens\" }");
        }

        if (name == null || name.isEmpty()) {
            return new ServerResponse(ServerResponse.FAILURE, "Missing body parameter: { \"name\": \"clemens\" }");
        }

        return null;
    }

    public static ServerResponse validateChatMessage(String message) {
        if (message == null || message.isEmpty()) {
            return new ServerResponse(ServerResponse.FAILURE, "Empty messages not allowed");
        }

        JsonObject json;
        try {
            json = JsonUtil.parse(message);
        } catch (Exception e) {
            return new ServerResponse(ServerResponse.FAILURE, e.getMessage());
        }
        String channel, msg, to;

        try {
            channel = json.getString(Constants.COM_CHANNEL);
        } catch (Exception e) {
            return new ServerResponse(ServerResponse.FAILURE, "Missing channel");
        }
        try {
            msg = json.getString(Constants.COM_MSG);
        } catch (Exception e) {
            return new ServerResponse(ServerResponse.FAILURE, "Missing message");
        }

        if (channel == null || channel.isEmpty()) {
            return new ServerResponse(ServerResponse.FAILURE, "Missing channel");
        }
        if (msg == null || msg.isEmpty()) {
            return new ServerResponse(ServerResponse.FAILURE, "Missing message");
        }

        if (!channel.equals(Constants.COM_CHANNEL_ALL) && !channel.equals(Constants.COM_CHANNEL_PRIVATE)) {
            return new ServerResponse(ServerResponse.FAILURE, "Unsupported channel");
        }

        if (channel.equals(Constants.COM_CHANNEL_PRIVATE)) {
            try {
                to = json.getString(Constants.COM_TO);
            } catch (Exception e) {
                return new ServerResponse(ServerResponse.FAILURE, "Missing to");
            }
            if (to == null || to.isEmpty()) {
                return new ServerResponse(ServerResponse.FAILURE, "Missing to");
            }
        }

        return null;
    }
}
