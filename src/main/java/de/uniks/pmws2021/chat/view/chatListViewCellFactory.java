package de.uniks.pmws2021.chat.view;

import de.uniks.pmws2021.chat.model.User;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class chatListViewCellFactory implements Callback<ListView<User>, ListCell<User>> {

    @Override
    public ListCell<User> call(ListView<User> param) {
        return new UserListCell();
    }

    // inner class
    private static class UserListCell extends ListCell<User> {

        @Override
        protected void updateItem(User item, boolean empty) {
            super.updateItem(item, empty);

            this.setText(null);
            this.setGraphic(null);
            if (!empty && item != null) {
                // makes my listView more sexy
                this.setText(item.getName() + " [" + (item.getStatus() ? "online" : "offline") + "]");
            }
        }
    }


}
