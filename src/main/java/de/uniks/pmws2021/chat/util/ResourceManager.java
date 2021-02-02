package de.uniks.pmws2021.chat.util;

import de.uniks.pmws2021.chat.model.User;
import org.fulib.yaml.YamlIdMap;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ResourceManager {
    // Choose your own SAVED_USERS_FOLDER_NAME and SAVED_USERS_FILE_NAME
    private static final String SAVED_USERS_FOLDER_NAME = "database";
    private static final String SAVED_USERS_FILE_NAME = "userDB.yaml";

    private static final Path USER_FOLDER = Path.of(SAVED_USERS_FOLDER_NAME);
    private static final Path USER_FILE = USER_FOLDER.resolve(SAVED_USERS_FILE_NAME);

    // static constructor magic to create the file if absent
    static {
        try {
            if (!Files.isDirectory(USER_FOLDER)) {
                Files.createDirectory(USER_FOLDER);
            }
            if (!Files.exists(USER_FILE)) {
                Files.createFile(USER_FILE);
            }
        } catch (Exception e) {
            System.err.println("Error while loading " + USER_FILE);
            e.printStackTrace();
        }
    }

    public static List<User> loadServerUsers() {
        List<User> userList = new ArrayList<>();

        try {
            // try to read userList from File
            String usersString = Files.readString(USER_FILE);

            // parse yaml-string to YamlIdMap
            YamlIdMap yamlIdMap = new YamlIdMap(User.class.getPackageName());

            // decode map
            yamlIdMap.decode(usersString);

            // map the decoded yaml data to real java objects and return list of heros
            for (Object object : yamlIdMap.getObjIdMap().values()
            ) {
                if (object instanceof  User) {
                    userList.add((User) object);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return userList;
    }

    public static void saveServerUsers(User user) {

        // save as .yaml
        try {
            // load all existing users
            List<User> oldUsers = loadServerUsers();

            // if user already in list dont save it
            oldUsers.removeIf(oldUser -> oldUser.getName().equals(user.getName()));

            // add copy to list
            oldUsers.add(user);

            // serialize as yaml
            YamlIdMap yamlIdMap = new YamlIdMap(User.class.getPackageName());
            yamlIdMap.discoverObjects(oldUsers);
            String yamlData = yamlIdMap.encode();
            Files.writeString(USER_FILE, yamlData);
        } catch (Exception e) {
            System.out.println("Error saving user");
            e.printStackTrace();
        }
    }

}
