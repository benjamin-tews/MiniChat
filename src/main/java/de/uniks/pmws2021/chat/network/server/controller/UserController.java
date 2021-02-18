package de.uniks.pmws2021.chat.network.server.controller;

import de.uniks.pmws2021.chat.ChatEditor;
import de.uniks.pmws2021.chat.model.Chat;
import de.uniks.pmws2021.chat.model.User;
import de.uniks.pmws2021.chat.network.server.websocket.ChatSocket;
import de.uniks.pmws2021.chat.util.JsonUtil;
import de.uniks.pmws2021.chat.util.ServerResponse;
import de.uniks.pmws2021.chat.util.ValidationUtil;
import spark.Request;
import spark.Response;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.util.ArrayList;
import java.util.List;

import static de.uniks.pmws2021.chat.Constants.COM_NAME;
import static de.uniks.pmws2021.chat.util.ServerResponse.SUCCESS;

public class UserController {
    private final Chat model;
    private final ChatEditor editor;
    private final ChatSocket chatSocket;

    public UserController(Chat model, ChatEditor editor, ChatSocket chatSocket) {
        this.model = model;
        this.editor = editor;
        this.chatSocket = chatSocket;
    }

    public String getAllLoggedInUsers(Request req, Response res) {
        /* get all users with status online*/

        List<User> users = new ArrayList<>();
        for (User user : this.editor.getUserList()
        ) {
            if (user.getStatus()) {
                users.add(user);
            }
        }

        if (users.isEmpty()) {
            ServerResponse err = new ServerResponse(ServerResponse.FAILURE, "No users online");
            return JsonUtil.stringify(err);
        } else {
            JsonArray bodyResult = JsonUtil.usersToJson(users);
            res.status(200);
            return JsonUtil.stringify(new ServerResponse(SUCCESS, bodyResult));
        }

    }

    public String login(Request req, Response res) {
        // Check for the body
        String body = req.body();
        ServerResponse err = ValidationUtil.validateBody(body);

        // Return on error
        if (err != null) {
            res.status(400);
            return JsonUtil.stringify(err);
        }

        // parse request body
        JsonObject parse = JsonUtil.parse(body);

        // get name from body
        String userName = parse.getString(COM_NAME);
        this.editor.haveUser(userName);

        // check if user already logged in, if yes, return with error message
        if (this.editor.getUser(userName).getStatus()) {
            // Forbidden
            res.status(403);
            err = new ServerResponse(ServerResponse.FAILURE, "User already logged on");
            return JsonUtil.stringify(err);
        } else {
            // set user online and save ip
            this.editor.getUser(userName).setStatus(true).setIp(req.ip());
            this.model.withAvailableUser(this.editor.getUser(userName));

            System.out.println("Login Routine läuft");

            // send login websocket message
            this.chatSocket.sendUserJoined(this.editor.getUser(userName));

            System.out.println("Login Routine läuft noch immer");

            // send response that everything went fine
            res.status(200);
            return JsonUtil.stringify(new ServerResponse(SUCCESS, JsonUtil.buildOkLogin()));
        }
    }

    public String logout(Request req, Response res) {
        // Check for the body
        String body = req.body();
        ServerResponse err = ValidationUtil.validateBody(body);

        // Return on error
        if (err != null) {
            res.status(400);
            return JsonUtil.stringify(err);
        }

        JsonObject parse = JsonUtil.parse(body);

        // get user by name
        String userName = parse.getString(COM_NAME);

        this.editor.haveUser(userName);

        // check if user already logged out, if yes, return with error message
        if (!(this.editor.getUser(userName).getStatus())) {
            // Not Acceptable
            res.status(406);
            err = new ServerResponse(ServerResponse.FAILURE, "User already logged out");
            return JsonUtil.stringify(err);
        }

        // end websocket connection of user
        chatSocket.killConnection(this.editor.getUser(userName), res.toString());

        // set user offline
        this.editor.getUser(userName).setStatus(false);

        // send logout websocket message
        chatSocket.sendUserLeft(this.editor.getUser(userName));

        // send response that everything went fine
        res.status(200);
        return JsonUtil.stringify(new ServerResponse(SUCCESS, JsonUtil.buildOkLogout()));
    }

}
