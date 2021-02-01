package de.uniks.pmws2021.chat;

import de.uniks.pmws2021.chat.model.*;

import java.util.ArrayList;

public class ChatEditor {
    private ArrayList<User> userList = new ArrayList<>();

    // ===========================================================================================
    // USER
    // ===========================================================================================

    public User haveUser(String name) {
        for (User user : userList) {
            if (user.getName().equals(name)) {
                return user;
            }
        }
        User user = new User();
        user.setName(name);
        // placeholder... ToDo maybe overload later
        user.setIp("127.0.0.1");
        userList.add(user);
        return user;
    }


    public ArrayList<User> getUserList() {
        return userList;
    }

    // ===========================================================================================
    // DUMMIES
    // ===========================================================================================

    public void createDummies() {
        User dummyUser1 = haveUser("dummy 1");
        User dummyUser2 = haveUser("dummy 2");
    }

}
