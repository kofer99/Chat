package gui.client;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;



public class Gui extends Application {
	BorderPane bordPane = new BorderPane();
	Scene scene = new Scene(bordPane, 512, 512);
	TextField textField = new TextField();
	static TextArea Chat = new TextArea();
	Button sendButton = new Button("Send");
	HBox messageSender = new HBox();
	Button menubtn = new Button("Menu");
	HBox menu = new HBox();
	ChatsClient cc ;
	ListView<ChatsClient> test = new ListView<ChatsClient>();

	final ObservableList<ChatsClient> data = FXCollections.observableArrayList(

	);

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		new ChatsServer();
		launch(args);
		
		
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		
		primaryStage.setScene(scene);
		
		primaryStage.show();
		setupGui();
		textField.setPromptText("Enter Message");
		Chat.setPromptText("Keine Nachrichten");
		setResize();
		StartupClient();


	}
	public static void printMessage(String message){
		if(Chat.getText().length()<= 0){
			Chat.setText(message);
		}else{
			Chat.setText(Chat.getText()+"\n"+ message);
		}
		
	}
	public void  StartupClient() {
		GridPane grid = new GridPane();
		Stage dialog = new Stage();
		dialog.initStyle(StageStyle.UTILITY);
		Scene scene = new Scene(grid,400,400);
		dialog.setScene(scene);
		Button ok = new Button("Ok");
		Label nickname_l = new Label("Nickname: ");

		TextField nickname = new TextField();

	//	vorname.setAccessibleText("bla");
		grid.add(nickname_l, 0, 0);

		grid.add(nickname, 1, 0);

		grid.add(ok,1,2);
		String name = "";
		dialog.show();
		
		
		
		ok.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if(nickname.getText().length()> 0){
				 cc = new ChatsClient(nickname.getText());
				data.add(cc);
				dialog.close();
				}
			}

		});
		
		
	}

	void setupGui() {
		
		bordPane.setTop(menu);
		menu.getChildren().add(menubtn);
		bordPane.setBottom(messageSender);
		messageSender.getChildren().addAll(textField, sendButton);
		bordPane.setCenter(Chat);
		Chat.setEditable(false);
		bordPane.setRight(test);
		test.setMaxWidth(90);

		test.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			System.out.println("Selected item: " + newValue);

			System.out.println("Selected item: " + newValue.getNickName());

		});

		test.setCellFactory(new Callback<ListView<ChatsClient>, ListCell<ChatsClient>>() {

			@Override
			public ListCell<ChatsClient> call(ListView<ChatsClient> param) {
				// TODO Auto-generated method stub
				return  new ListCell<ChatsClient>(){
					protected void updateItem(ChatsClient item, boolean empty) {
					     super.updateItem(item, empty);
					
					     if (empty || item == null) {
					         setText(null);
					     } else {
					         setText(item.getNickName());
					     }
					 }

				};}	});



		test.setEditable(true);

		test.setItems(data);

		sendButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if(textField.getText().length()>= 0){
					cc.SendMessage(textField.getText());
				}
			}

		});


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
