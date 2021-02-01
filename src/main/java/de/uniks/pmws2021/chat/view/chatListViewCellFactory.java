package de.uniks.pmws2021.chat.view;

import de.uniks.pmws2021.chat.model.User;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

public class chatListViewCellFactory implements javafx.util.Callback<ListView<User>, ListCell<User> > {

    @Override
    public ListCell<User> call (ListView<User> param) {
        return new UserListCell();
    }

    // inner class
    private static class UserListCell extends ListCell<User> {
        @Override
        protected void updateItem(User item, boolean empty) {
            super.updateItem(item, empty);
            if (!empty){
                // makes my listView more sexy
                this.setText(item.getName());
            }
        }
    }


}
