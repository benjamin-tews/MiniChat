package de.uniks.pmws2021.chat;

import de.uniks.pmws2021.chat.model.*;
import java.util.ArrayList;
import java.util.List;

public class ChatEditor {
    public ArrayList<User> userList = new ArrayList<>();

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
        user.setIp(ip);
        userList.add(user);
        return user;
    }

    // overload haveUser
    public User haveUser(User newUser) {
        for (User user : userList) {
            if (user.getName().equals(newUser.getName())) {
                return user;
            }
        }
        User user = new User();
        user.setName(newUser.getName());
        user.setIp(newUser.getIp());
        user.setStatus(newUser.getStatus());
        user.setChat(newUser.getChat());
        userList.add(user);
        return user;
    }


    public List<User> getUserList() {
        return userList;
    }

    public Chat getChatServerList() {
        return null;
    }

}
