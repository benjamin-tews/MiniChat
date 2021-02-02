package de.uniks.pmws2021.chat;

import de.uniks.pmws2021.chat.controller.subcontroller.ServerViewSubController;
import de.uniks.pmws2021.chat.model.*;

import java.util.ArrayList;

public class ChatEditor {
    private ArrayList<User> userList = new ArrayList<>();

    // ===========================================================================================
    // USER
    // ===========================================================================================

    public User haveUser(String name, String ip) {
        for (User user : userList) {
            if (user.getName().equals(name)) {
                return user;
            }
        }
        User user = new User();
        user.setName(name);
        // placeholder... ToDo maybe overload later
        user.setIp(ip);
        userList.add(user);
        return user;
    }

    public ArrayList<User> getUserList() {
        return userList;
    }

    public Chat getChatServerList() {
        return null;
    }
}
