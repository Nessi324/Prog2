package adressbuch;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class ErrorViewController implements Initializable {

    @FXML
    Label fehler;
    private String error;
    
    public ErrorViewController(String error) {
        this.error = error;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        fehler.setText(error);
    }    
    
}
