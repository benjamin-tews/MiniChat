package de.uniks.pmws2021.chat;

import de.uniks.pmws2021.chat.model.*;

import java.util.ArrayList;

public class ChatEditor {

    private ArrayList<User> userList = new ArrayList<>();

    // dummy haveUser
    public void haveUser(String name) {
        for (User user : userList) {
            if (user.getName().equals(name)) {
                return;
            }
        }
        User user = new User();
        user.setName(name);
        userList.add(user);
    }

}
