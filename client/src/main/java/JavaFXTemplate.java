import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class JavaFXTemplate extends Application {
    
    public static PokerInfo clientPokerInfo;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		clientPokerInfo = new PokerInfo();
		
		Parent root = FXMLLoader.load(getClass().getResource("ClientStart.fxml"));
		Scene scene = new Scene(root, 1000, 700);
		
		primaryStage.setTitle("Three Card Poker");
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
		
		primaryStage.setOnCloseRequest(event -> {
			System.exit(0);
		});
	}
	
	public static void setPokerInfo(PokerInfo info) {
		clientPokerInfo = info;
	}
}
