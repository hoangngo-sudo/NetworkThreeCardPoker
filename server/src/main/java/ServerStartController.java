import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ServerStartController {
    
    @FXML
    private TextField portField;
    
    @FXML
    private Button startButton;
    
    @FXML
    private VBox startRoot;
    
    @FXML
    public void initialize() {
        // Check if startRoot is available; if not, we'll try to get it from portField's parent
        Platform.runLater(() -> {
            if (startRoot == null && portField != null && portField.getParent() != null) {
                // Navigate up to find the root VBox
                javafx.scene.Parent parent = portField.getParent();
                while (parent != null && !(parent instanceof VBox && parent.getStyleClass().contains("start-root"))) {
                    parent = parent.getParent();
                }
                if (parent instanceof VBox) {
                    final VBox root = (VBox) parent;
                    root.setOnMousePressed(event -> root.requestFocus());
                }
            } else if (startRoot != null) {
                startRoot.setOnMousePressed(event -> startRoot.requestFocus());
            }
        });
    }
    
    @FXML
    private void handleStartServer() {
        String portText = portField.getText().trim();
        
        if (portText.isEmpty()) {
            portText = "5555";
        }
        
        try {
            int port = Integer.parseInt(portText);
            if (port < 1024 || port > 65535) {
                showError("Invalid Port", "Port must be between 1024 and 65535");
                return;
            }
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ServerGame.fxml"));
            Parent root = loader.load();
            
            ServerGameController gameController = loader.getController();
            
            Server serverConnection = new Server(data -> {
                Platform.runLater(() -> {
                    gameController.addLogMessage(data);
                });
            }, portText);
            
            gameController.setServer(serverConnection);
            JavaFXTemplate.setGameController(gameController);
            
            Scene scene = new Scene(root, 1000, 700);
            Stage stage = (Stage) startButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setTitle("Three Card Poker Server");
            
            serverConnection.startServer();
            
        } catch (NumberFormatException e) {
            showError("Invalid Port", "Please enter a valid port number");
        } catch (Exception e) {
            showError("Server Error", "Failed to start server: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
