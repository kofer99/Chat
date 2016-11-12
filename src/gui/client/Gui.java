package gui.client;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

public class Gui extends Application {
	BorderPane bordPane = new BorderPane();
	Scene scene = new Scene(bordPane, 512, 512);
	TextField textField = new TextField("EnterStuff");
	TextArea Chat = new TextArea("EnterStuff");
	Button sendButton = new Button("Send");
	HBox messageSender = new HBox();
	Button menubtn = new Button("Menu");
	HBox menu = new HBox();

	ListView<Client> test = new ListView<Client>();

	final ObservableList<Client> data = FXCollections.observableArrayList(

	);

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		primaryStage.setScene(scene);
		primaryStage.show();
		setupGui();
		textField.setText("asfasfas");
		setResize();

	}

	void setupGui() {
		bordPane.setTop(menu);
		menu.getChildren().add(menubtn);
		bordPane.setBottom(messageSender);
		messageSender.getChildren().addAll(textField, sendButton);
		bordPane.setCenter(Chat);
		Chat.setEditable(false);
		bordPane.setRight(test);

		test.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			System.out.println("Selected item: " + newValue);

			System.out.println("Selected item: " + newValue.getNicknameValue());

		});

		test.setCellFactory(new Callback<ListView<Client>, ListCell<Client>>() {

			@Override
			public ListCell<Client> call(ListView<Client> param) {
				// TODO Auto-generated method stub
				return  new ListCell<Client>(){
					protected void updateItem(Client item, boolean empty) {
					     super.updateItem(item, empty);
					     if(item == null){
					    	 System.out.println("haha");
					     }
					     if (empty || item == null) {
					         setText(null);
					     } else {
					         setText(item.getNicknameValue());
					     }
					 }

				};}	});

		// Bindings.bindBidirectional(test.accessibleTextProperty(),
		// Client.nickname);
		// test.accessibleTextProperty().set(Client.getNicknameValue());

		// test.getColumns().add(nickcl);

		test.setEditable(true);

		test.setItems(data);

		Client ac = new Client("asdas");
		Client cc = new Client("BAcka");
		data.add(ac);
		data.add(cc);
		// users.setText("ssssssssss");
		// bordPane.setBottom(sendButton);

	}

	void setResize() {
		try {
			scene.widthProperty().addListener(new ChangeListener<Number>() {
				@Override
				public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth,
						Number newSceneWidth) {
					// update_gui();
					System.out.println("Width: " + newSceneWidth);
				}

			});
			scene.heightProperty().addListener(new ChangeListener<Number>() {
				@Override
				public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight,
						Number newSceneHeight) {
					System.out.println("Height: " + newSceneHeight);
					// update_gui();
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
