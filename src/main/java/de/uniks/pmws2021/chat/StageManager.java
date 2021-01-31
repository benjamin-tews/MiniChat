package de.uniks.pmws2021.chat;

import de.uniks.pmws2021.chat.controller.StartViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class StageManager extends Application {

    private static Stage stage;
    //private static DungeonScreenController dungeonCtrl;
    private static ChatEditor chatEditor = new ChatEditor();
    private static StartViewController startController;

    public static void showMiniChatStart() {
      //  cleanup();
        // show hero screen
        // load view
        try {
            Parent root = FXMLLoader.load(StageManager.class.getResource("view/MiniChatStart.fxml"));
            Scene scene = new Scene(root);

            // editor

            // init controller
            startController = new StartViewController(root, chatEditor, stage);
            startController.init();

            // display
            stage.setTitle("PMWS2021 - Mini Chat");
            stage.setScene(scene);
            stage.centerOnScreen();

        } catch (Exception e) {
            System.err.println("Failed to load Hero Screen :: showHeroScreen");
            e.printStackTrace();
        }
    }


/*
    private static void cleanup() {
        // call cascading stop
        if (dungeonCtrl != null) {
            dungeonCtrl.stop();
            dungeonCtrl = null;
        }
        if (heroCtrl != null) {
            heroCtrl.stop();
            heroCtrl = null;
        }
    }
*/
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
    //    cleanup();
    }

    public ChatEditor getModel() {
        return chatEditor;
    }
}
