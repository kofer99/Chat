package gui.client;

import java.util.ArrayList;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

/**
 * Lukas Franke, Daniel Neufeld
 */
public class Gui extends Application
{
	BorderPane bordPane = new BorderPane();
	Scene scene = new Scene(bordPane, 512, 512);
	TextField textField = new TextField();
	TextArea Chat = new TextArea();
	Button sendButton = new Button("Send");
	HBox messageSender = new HBox();
	Button menubtn = new Button("Menu");
	HBox menu = new HBox();
	ChatsClient cc;
	ListView<String> nickList = new ListView<String>();
	NameUpdater nameUpdater;

	public static void main(String[] args)
	{
		launch(args);
	}

	// TODO: We don't want to store this here
	ChatsServer server;

	@Override
	public void start(Stage primaryStage) throws Exception {
		server = new ChatsServer();
		cc = new ChatsClient(this);

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>()
		{
			@Override
			public void handle(WindowEvent event)
			{
				server.Stop();
				cc.Disconnect();
			}
		});

		primaryStage.setScene(scene);
		StartupClient(primaryStage);
	}

	public void printMessage(String message)
	{
		Chat.setText(Chat.getText() + message);
	}

	public void StartupClient(Stage primaryStage)
	{
		GridPane grid = new GridPane();
		Stage dialog = new Stage();
		dialog.initStyle(StageStyle.UTILITY);
		Scene scene = new Scene(grid, 230, 90);
		dialog.setScene(scene);

		Button ok = new Button("Connect");
		Label nickname_l = new Label("Nickname: ");
		TextField nickname = new TextField();
		nickname.setText("Newbie");
		Label IpAddresse = new Label("Ip: ");
		TextField Ip = new TextField();
		Ip.setText("localhost");

		grid.add(nickname_l, 0, 0);
		grid.add(nickname, 1, 0);
		grid.add(IpAddresse, 0, 1);
		grid.add(Ip, 1, 1);
		grid.add(ok, 1, 2);
		dialog.show();

		ok.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				String nick = nickname.getText();
				if (nick.length() <= 0)
					return;

				cc.SetNickName(nick);

				primaryStage.show();
				setupGui();
				setResize();

				dialog.close();
			}
		});
	}

	void updateNickList(ArrayList<String> newNicks)
	{
		nickList.setItems(FXCollections.observableArrayList(newNicks));
	}

	void setupGui()
	{
		textField.setPromptText("Enter Message");
		Chat.setPromptText("Keine Nachrichten");

		scene.getStylesheets().add("gui/client/application.css");

		bordPane.setTop(menu);
		menu.getChildren().add(menubtn);
		bordPane.setBottom(messageSender);
		messageSender.getChildren().addAll(textField, sendButton);
		bordPane.setCenter(Chat);
		Chat.setEditable(false);
		bordPane.setRight(nickList);
		nickList.setMaxWidth(90);

		nickList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			System.out.println("Selected item: " + newValue);
		});

		nickList.setCellFactory(new Callback<ListView<String>, ListCell<String>>()
		{
			@Override
			public ListCell<String> call(ListView<String> param)
			{
				return new ListCell<String>()
				{
					protected void updateItem(String item, boolean empty)
					{
						super.updateItem(item, empty);
						setText(item);
					}
				};
			}
		});

		// This was 'true' before. Why?
		nickList.setEditable(false);

		nameUpdater = new NameUpdater(cc, this);
		nameUpdater.start();

		sendButton.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				String input = textField.getText();
				if (input.length() >= 0)
					cc.SendMessage(input);
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

class NameUpdater extends Thread
{
	ArrayList<String> currentNicks;
	ChatsClient client;
	Gui gui;

	public NameUpdater(ChatsClient client, Gui gui)
	{
		this.client = client;
		this.gui = gui;
	}

	public void run()
	{
		while (client.IsConnected)
		{
			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}

			String[] nicks = client.ConnectedNames;
			if (nicks == null || nicks.length == 0)
				continue;

			currentNicks = new ArrayList<String>();
			for (int i = 0; i < nicks.length; i++)
				currentNicks.add(nicks[i]);

			Platform.runLater(() -> gui.updateNickList(currentNicks));
		}
	}
}
