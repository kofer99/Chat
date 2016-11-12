package gui.client;

import javafx.event.EventHandler;
import javafx.scene.control.ListCell;

public class DefListCell<Client> extends ListCell<Client> {

	@Override public void updateItem(Client item, boolean empty)

	 {
		super.updateItem(item, empty);

		if (empty || item == null) {
			setText(null);
		} else {


		}
	}
}
