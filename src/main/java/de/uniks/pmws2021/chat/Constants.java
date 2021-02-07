package de.uniks.pmws2021.chat;

public class Constants {
    // Server
    public static final String SERVER_CONFIG_PATH = "server.yaml";
    public static final int SERVER_PORT = 3000;
    public static final String CHAT_WEBSOCKET_PATH = "/ws/chat";
    public static final String API_PREFIX = "/api";
    public static final String USERS_PATH = "/users";
    public static final String LOGIN_PATH = "/login";
    public static final String LOGOUT_PATH = "/logout";

    // Communication
    public static final String COM_NAME = "name";
    public static final String COM_IP = "ip";
    public static final String COM_STATUS = "status";
    public static final String COM_URL = "url";
    public static final String COM_MSG = "msg";
    public static final String COM_SALUTE = "bye";
    public static final String COM_NOOP = "noop";
    public static final String COM_CHANNEL_ALL = "all";
    public static final String COM_CHANNEL_PRIVATE = "private";
    public static final String COM_CHANNEL_SYSTEM = "system";
    public static final String COM_TO = "to";
    public static final String COM_FROM = "from";
    public static final String COM_DATA = "data";
    public static final String COM_CHANNEL = "channel";
    public static final String COM_SERVER = "server";
    public static final String COM_ACTION = "action";
    public static final String COM_USER_JOINED = "joined";
    public static final String COM_USER_LEFT = "left";
    public static final String COM_USER = "user";

    // Client
    public static final String REST_SERVER_URL = "http://localhost:3000";
    public static final String WS_SERVER_URL = "ws://localhost:3000";
    public static final String CHAT_SOCKET_URL = WS_SERVER_URL + CHAT_WEBSOCKET_PATH;

}
