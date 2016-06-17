package terminplaner;

import adressbuch.Adressbuch;
import Exceptions.UngueltigerSchluesselException;
import adressbuch.AdressbuchViewController;
import adressbuch.ViewHelper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 * FXML Controller class fuer die Terminplaner-Hauptansicht.
 *
 * @author beuth
 */
public class PlanerViewController implements Initializable {

    @FXML
    private Button addButton;
    @FXML
    private ListView<Termin> terminliste;
    @FXML
    private DatePicker date;
    @FXML
    private MenuBar menuBar;

    private Terminplaner planer;
    private Adressbuch adressen;
    private ObservableList<Termin> terminData;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        adressen = new Adressbuch();
        try {
            planer = new Terminplaner(adressen.getKontakt("john"));
        } catch (UngueltigerSchluesselException ex) {
            System.out.println(ex.toString());
        }
        date.setValue(LocalDate.now());
        showTermine();
        date.setOnAction((e) -> showTermine());
        addButton.setOnAction((e) -> addTermin());
        configureMenue();
        configureList();

    }

    private void showTermine() {
        terminliste.getItems().clear();
        if (getSelectedDate() != null) {
            LocalDate datum = getSelectedDate();
            List<Termin> liste = planer.getTermineTag(datum);
            if (liste != null) {
                terminData.addAll(liste);
                terminliste.setItems(terminData);
            }
        }
    }

    private void addTermin() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void configureMenue() {
        menuBar.getMenus().get(0).getItems().add(new MenuItem("Laden"));
        menuBar.getMenus().get(0).getItems().get(0).setOnAction((value) -> loadTermine());
        menuBar.getMenus().get(0).getItems().add(new MenuItem("Speichern"));
        menuBar.getMenus().get(0).getItems().get(1).setOnAction((value) -> saveTermine());
        menuBar.getMenus().get(1).getItems().add(new MenuItem("Kontakte Bearbeiten"));
        menuBar.getMenus().get(1).getItems().get(0).setOnAction((value) -> editKontakte());
    }

    private void configureList() {
        terminData = FXCollections.observableArrayList();
        terminliste.setItems(terminData);
        ObservableList<Termin> clicked = terminliste.getSelectionModel().getSelectedItems();
        clicked.addListener((ListChangeListener.Change<? extends Termin> listener) -> editTermin());
    }

    private void loadTermine() {
        // planer.load(file);
    }

    private void saveTermine(){
        try {
            Stage stage = new Stage();
            stage.setTitle("Open New Directory");
            DirectoryChooser fs = new DirectoryChooser();
            File file = fs.showDialog(stage);
            FileOutputStream fmf = new FileOutputStream(file.getPath()+"\\Termine.ser");
            ObjectOutputStream os = new ObjectOutputStream(fmf);
            Termin[] termine = planer.getAllTermine();
            for(Termin x : termine){
                os.writeObject(x);
            }
            os.close();
        } catch (IOException ex) {
            Logger.getLogger(PlanerViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void editKontakte() {
        URL url = adressen.getClass().getResource("adressbuchView.fxml");
        AdressbuchViewController controller = new AdressbuchViewController(this);
        ViewHelper.showView(controller, url);
    }

    public Adressbuch getAdressbuch() {
        adressen = new Adressbuch();
        return adressen;
    }

    private void editTermin() {
        Termin termin = terminliste.getSelectionModel().getSelectedItem();
    }

    public LocalDate getSelectedDate() {
        return date.getValue();
    }
}
