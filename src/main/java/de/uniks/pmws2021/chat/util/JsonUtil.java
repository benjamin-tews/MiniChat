package de.uniks.pmws2021.chat.util;

import de.uniks.pmws2021.chat.Constants;
import de.uniks.pmws2021.chat.model.User;
import org.json.JSONObject;

import javax.json.*;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class JsonUtil {
    public static JsonObject parse(JSONObject hereticalJsonObject) {
        return Json.createReader(new StringReader(hereticalJsonObject.toString())).readObject();
    }

    public static JsonObject parse(String json) {
        return Json.createReader(new StringReader(json)).readObject();
    }

    public static String stringify(ServerResponse res) {
        JsonStructure builder = Json.createObjectBuilder()
                .add(Constants.COM_STATUS, res.getStatus())
                .add(Constants.COM_MSG, res.getMessage())
                .add(Constants.COM_DATA, res.getJsonData())
                .build();

        return builder.toString();
    }

    public static JsonArray usersToJson(List<User> users) {
        JsonArrayBuilder ab = Json.createArrayBuilder();
        users.forEach((user) -> ab.add(userToJson(user)));

        return ab.build();
    }

    public static JsonObject userToJson(User user) {
        return Json.createObjectBuilder()
                .add(Constants.COM_NAME, user.getName())
                .add(Constants.COM_IP, user.getIp())
                .add(Constants.COM_STATUS, user.getStatus())
                .build();
    }

    public static List<User> parseUsers(JsonArray usersJson) {
        List<User> users = new ArrayList<>();
        for (JsonValue val : usersJson) {
            JsonObject user = val.asJsonObject();
            users.add(parseUser(user));
        }
        return users;
    }

    public static User parseUser(JsonObject userJson) {
        return new User()
                .setName(userJson.getString(Constants.COM_NAME))
                .setIp(userJson.getString(Constants.COM_IP))
                .setStatus(userJson.getBoolean(Constants.COM_STATUS));
    }

    public static JsonObject buildOkLogin() {
        return Json.createObjectBuilder().add(Constants.COM_URL, Constants.CHAT_WEBSOCKET_PATH).build();
    }

    public static JsonObject buildOkLogout() {
        return Json.createObjectBuilder().add(Constants.COM_MSG, Constants.COM_SALUTE).build();
    }

    public static JsonObject buildUserJoinedSystemMessage(User user) {
        return Json.createObjectBuilder()
                .add(Constants.COM_CHANNEL, Constants.COM_CHANNEL_SYSTEM)
                .add(Constants.COM_FROM, Constants.COM_SERVER)
                .add(Constants.COM_DATA, Json.createObjectBuilder()
                        .add(Constants.COM_ACTION, Constants.COM_USER_JOINED)
                        .add(Constants.COM_USER, userToJson(user))
                )
                .build();
    }

    public static JsonObject buildUserLeftSystemMessage(User user) {
        return Json.createObjectBuilder()
                .add(Constants.COM_CHANNEL, Constants.COM_CHANNEL_SYSTEM)
                .add(Constants.COM_FROM, Constants.COM_SERVER)
                .add(Constants.COM_DATA, Json.createObjectBuilder()
                        .add(Constants.COM_ACTION, Constants.COM_USER_LEFT)
                        .add(Constants.COM_USER, userToJson(user))
                )
                .build();
    }

    public static JsonObject buildPublicChatMessage(String message) {
        return Json.createObjectBuilder()
                .add(Constants.COM_CHANNEL, Constants.COM_CHANNEL_ALL)
                .add(Constants.COM_MSG, message)
                .build();
    }

    public static JsonObject buildPrivateChatMessage(String message, String to) {
        return Json.createObjectBuilder()
                .add(Constants.COM_CHANNEL, Constants.COM_CHANNEL_PRIVATE)
                .add(Constants.COM_TO, to)
                .add(Constants.COM_MSG, message)
                .build();
    }
}
