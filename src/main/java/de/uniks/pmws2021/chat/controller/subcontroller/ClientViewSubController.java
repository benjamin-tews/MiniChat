package de.uniks.pmws2021.chat.controller.subcontroller;

import de.uniks.pmws2021.chat.ChatEditor;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class ClientViewSubController {

    private Parent view;
    private ChatEditor editor;
    private Stage stage;

    public ClientViewSubController(Parent view, ChatEditor editor, Stage stage) {
        this.view = view;
        this.editor = editor;
        this.stage = stage;
    }


}
