package de.uniks.pmws2021.chat.util;

import de.uniks.pmws2021.chat.ChatEditor;
import de.uniks.pmws2021.chat.model.Chat;
import de.uniks.pmws2021.chat.model.User;
import org.junit.Assert;
import org.junit.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ResourceManagerTest {

    private static final Path USER_FOLDER = Path.of("database");
    private static final Path USER_FILE = USER_FOLDER.resolve("userDB.yaml");

    @Test
    public void saveLoadTest() {

        // delete file if exists
        try {
            Files.deleteIfExists(USER_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // create users and chat
        ChatEditor chatEditor = new ChatEditor();

        Chat chat = new Chat();

        chatEditor.haveUser("Benjamin", "127.0.0.1");
        chatEditor.haveUser("Anton", "127.0.0.1");
        chatEditor.haveUser("Hannah", "127.0.0.1");

        for (User user : chatEditor.getUserList()
            ) {
                ResourceManager.saveServerUsers(user);
            }

        // load users
        List<User> users = ResourceManager.loadServerUsers();

        User user0 = users.get(0);
        User user1 = users.get(1);
        User user2 = users.get(2);

        chat.withAvailableUser(user0, user2);

        // user Asserts
        Assert.assertEquals(3, users.stream().count());
        Assert.assertEquals("127.0.0.1", user0.getIp());
        Assert.assertTrue((user0.getName().equals("Benjamin")) || (user1.getName().equals("Benjamin")) || (user2.getName().equals("Benjamin")));
        Assert.assertTrue((user0.getName().equals("Benjamin")) || (user0.getName().equals("Hannah")) || (user0.getName().equals("Anton")));

        // chat Asserts
        Assert.assertEquals(2, chat.getAvailableUser().size());
    }

}