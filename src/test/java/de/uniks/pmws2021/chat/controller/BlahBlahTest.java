package de.uniks.pmws2021.chat.controller;


import de.uniks.pmws2021.chat.model.Chat;
import de.uniks.pmws2021.chat.model.User;
import org.fulib.FulibTools;
import org.junit.Test;

public class BlahBlahTest {

    @Test
    public void StageManagerTest() {
        User user = new User();
        Chat chat = new Chat();
        user.setChat(chat);
        user.setIp("127.0.0.1");
        user.setStatus(true);
        FulibTools.objectDiagrams().dumpSVG("diagrams/start.svg", user, chat);
    }

}