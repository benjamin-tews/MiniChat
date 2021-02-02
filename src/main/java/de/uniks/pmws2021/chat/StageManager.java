package de.uniks.pmws2021.chat;

import de.uniks.pmws2021.chat.controller.StartViewController;
import de.uniks.pmws2021.chat.controller.subcontroller.ClientViewSubController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class StageManager extends Application {

    public static Stage stage;

    private static ChatEditor chatEditor = new ChatEditor();
    private static StartViewController startController;

    public static void showMiniChatStart() {
        cleanup();

        // load view
        try {
            Parent root = FXMLLoader.load(StageManager.class.getResource("view/MiniChatStart.fxml"));
            Scene scene = new Scene(root);

            // editor

            // init controller
            startController = new StartViewController(root, chatEditor);
            startController.init();

            // display
            stage.setTitle("PMWS2021 - Mini Chat");
            stage.setScene(scene);
            stage.centerOnScreen();

        } catch (Exception e) {
            System.err.println("Failed to load MiniChatStart :: showMiniChatStart");
            e.printStackTrace();
        }
    }

    private static void cleanup() {
        // call cascading stop
        if (startController != null) {
            startController.stop();
            startController = null;
        }
    }

    @Override
    public void start(Stage primaryStage) {
        // start application
        stage = primaryStage;
        showMiniChatStart();
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

    public ChatEditor getModel() {
        return chatEditor;
    }

}
