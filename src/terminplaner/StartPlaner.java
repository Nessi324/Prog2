package terminplaner;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Startklasse für die Terminplaner-Anwendung.
 * @author beuth
 */
public class StartPlaner extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
       primaryStage.setTitle("");
       Pane myPane = (Pane)FXMLLoader.load(getClass().getResource("planerView.fxml"));
       Scene myScene = new Scene(myPane);
       primaryStage.setScene(myScene);
       primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
