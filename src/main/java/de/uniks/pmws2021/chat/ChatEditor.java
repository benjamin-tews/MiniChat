package de.uniks.pmws2021.chat;

import de.uniks.pmws2021.chat.model.*;
import java.util.ArrayList;
import java.util.List;

public class ChatEditor {
    public ArrayList<User> userList = new ArrayList<>();

    // ===========================================================================================
    // CHAT
    // ===========================================================================================


    // ===========================================================================================
    // USER
    // ===========================================================================================

    public User haveUser(String name) {
        for (User user : userList) {
            if (user.getName().equals(name)) {
                return user;
            }
        }
        // new user
        User user = new User();
        user.setName(name);
        user.setStatus(false);
        userList.add(user);
        return user;
    }

    public User getUser (String name) {
        for (User user : userList) {
            if (user.getName().equals(name)) {
                return user;
            }
        }
        return null;
    }


    public List<User> getUserList() {
        return userList;
    }

}
