import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class JavaFXTemplate extends Application {
    
    private static ServerGameController gameController;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("ServerStart.fxml"));
		Scene scene = new Scene(root, 1000, 700);
		
		primaryStage.setTitle("Three Card Poker Server");
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
		
		primaryStage.setOnCloseRequest(event -> {
			System.exit(0);
		});
	}
	
	public static void updateClientCount(int size) {
		if (gameController != null) {
			gameController.updateClientCount(size);
		}
	}
	
	public static void setGameController(ServerGameController controller) {
		gameController = controller;
	}
}
