package terminplaner;

import Exceptions.TerminUeberschneidungException;
import adressbuch.Adressbuch;
import Exceptions.UngueltigerSchluesselException;
import adressbuch.AdressbuchViewController;
import adressbuch.ViewHelper;
import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

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
    public ObservableList<Termin> terminData;

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
        date.setOnAction((e) -> showTermine());
        addButton.setOnAction((e) -> addTermin());
        configureList();
        configureMenu();
        showTermine();

    }

    private void showTermine() {
        if (getSelectedDate() != null) {
            terminData.clear();
            LocalDate datum = getSelectedDate();
            List<Termin> liste = planer.getTermineTag(datum);
            if (liste != null && liste.size() > 0) {
                terminData.addAll(liste);
            }
        }
    }

    private void addTermin() {
        TerminViewController controller = new TerminViewController(null, this);
        URL url = controller.getClass().getResource("terminView.fxml");
        ViewHelper.showView(controller, url);
    }

    private void configureMenu() {
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
        terminliste.setOnMouseClicked((value) -> editTermin());
    }

    private void loadTermine() {
        try {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                planer.load(file);
            }
            showTermine();
        } catch (Exception ex) {
            ViewHelper.showError("Datei konnte nicht geladen werden.");
        }
    }

    public void processTermin(Termin newtermin) throws TerminUeberschneidungException {
        planer.setTermin(newtermin);
        showTermine();
    }

    private void saveTermine() {
        FileChooser fs = new FileChooser();
        fs.setTitle("Speicher Termine");
        File file = fs.showSaveDialog(null);
        if (file != null) {
            try {
                planer.save(file);
            } catch (IOException ex) {
                ViewHelper.showError("Datei konnte nicht gespeichert werden.");
            }
        }
    }

    private void editKontakte() {
        URL url = adressen.getClass().getResource("adressbuchView.fxml");
        AdressbuchViewController controller = new AdressbuchViewController(adressen);
        ViewHelper.showView(controller, url);
    }

    public Adressbuch getAdressbuch() {
        return adressen;
    }

    private void editTermin() {
        Termin termin = terminliste.getSelectionModel().getSelectedItem();
        if (termin != null) {
            if (planer.updateErlaubt(termin)) {
                TerminViewController controller = new TerminViewController(termin, this);
                URL url = controller.getClass().getResource("terminView.fxml");
                ViewHelper.showView(controller, url);
            }
            else {
                TerminViewController controller = new TerminViewController(termin, null);
                URL url = controller.getClass().getResource("terminView.fxml");
                ViewHelper.showView(controller, url);
            }
        }
    }

    public LocalDate getSelectedDate() {
        return date.getValue();
    }
}
