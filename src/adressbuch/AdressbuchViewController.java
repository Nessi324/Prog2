package adressbuch;

import Exceptions.DoppelterSchluesselException;
import Exceptions.UngueltigerSchluesselException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import terminplaner.PlanerViewController;

/**
 *
 * @author Nessi
 */
public class AdressbuchViewController implements Initializable {

    @FXML
    private TextField searchField;
    @FXML
    private TableView<Kontakt> tableView;
    @FXML
    private TableColumn<Kontakt, String> name;
    @FXML
    private TableColumn<Kontakt, String> phone;
    @FXML
    private TableColumn<Kontakt, String> email;
    @FXML
    private TextField nameField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField emailField;
    @FXML
    private Button addButton;

    private String prompter = "Suche mit einem Namen starten....";
    private Adressbuch adressbuch;
    private ObservableList<Kontakt> tableContent;

    public AdressbuchViewController(Adressbuch aThis) {
        adressbuch = aThis;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configureTable();
        inItNewKontakt();
        showKontakte(adressbuch.getAlleKontakte());
        inItSearch();

    }

    private void showKontakte(Kontakt[] testdaten) {
        tableContent.clear();
        tableContent.addAll(testdaten);
    }

    private void inItNewKontakt() {
        nameField.setPromptText("Name");
        phoneField.setPromptText("Telefon");
        emailField.setPromptText("Email");
        addButton.setOnAction((e) -> {
            try {
                addKontakt();
            } catch (DoppelterSchluesselException eve) {
                ViewHelper.showError(eve.toString());
            } catch (IllegalStateException eve) {
                ViewHelper.showError(eve.getMessage());
            }
        });
    }

    private void addKontakt() throws DoppelterSchluesselException {
        if (nameField.getText().length() > 0 || phoneField.getText().length() > 0 && emailField.getText().length() > 0) {
            Kontakt newKontakt = new Kontakt(nameField.getText(), phoneField.getText(), emailField.getText());
            adressbuch.addKontakt(newKontakt);
            nameField.clear();
            phoneField.clear();
            emailField.clear();
        }
        showKontakte(adressbuch.getAlleKontakte());
    }

    private void configureTable() {
        tableContent = FXCollections.observableArrayList();
        Kontakt[] kontakte = adressbuch.getAlleKontakte();
        tableContent.addAll(kontakte);
        tableView.setItems(tableContent);
        tableView.setEditable(true);
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        phone.setCellValueFactory(new PropertyValueFactory<>("telefon"));
        email.setCellValueFactory(new PropertyValueFactory<>("email"));
        name.setCellFactory(TextFieldTableCell.<Kontakt>forTableColumn());
        name.setOnEditCommit((e) -> updateName(e));
        phone.setCellFactory(TextFieldTableCell.<Kontakt>forTableColumn());
        phone.setOnEditCommit((e) -> updatePhone(e));
        email.setCellFactory(TextFieldTableCell.<Kontakt>forTableColumn());
        email.setOnEditCommit((e) -> updateEmail(e));
    }

    private void filterList() {
        String suche = searchField.getText();
        Kontakt[] ergebnis = adressbuch.getKontakte(suche);
        showKontakte(ergebnis);
    }

    private void updateName(TableColumn.CellEditEvent<Kontakt, String> e) {
        try {
            String alt = e.getOldValue();
            String neu = e.getNewValue();
            if (alt.equals(neu)) {
                return;
            }
            Kontakt altKontakt = e.getRowValue();
            Kontakt ersatz = new Kontakt(neu, altKontakt.getTelefon(), altKontakt.getEmail());
            if (altKontakt != ersatz) {
                adressbuch.updateKontakt(getKey(altKontakt), ersatz);
            }
        } catch (UngueltigerSchluesselException ex) {
            ViewHelper.showError(ex.toString());
        }
        showKontakte(adressbuch.getAlleKontakte());
    }

    private void updateEmail(TableColumn.CellEditEvent<Kontakt, String> e) {
        String alt = e.getOldValue();
        String neu = e.getNewValue();
        if (alt.equals(neu)) {
            return;
        }
        Kontakt altKontakt = e.getRowValue();
        altKontakt.setEmail(neu);
        showKontakte(adressbuch.getAlleKontakte());
    }

    private void updatePhone(TableColumn.CellEditEvent<Kontakt, String> e) {
        try {
            String alt = e.getOldValue();
            String neu = e.getNewValue();
            if (alt.equals(neu)) {
                return;
            }
            Kontakt altKontakt = e.getRowValue();
            Kontakt ersatz = new Kontakt(altKontakt.getName(), neu, altKontakt.getEmail());
            if (altKontakt != ersatz) {
                adressbuch.updateKontakt(getKey(altKontakt), ersatz);
            }
        } catch (UngueltigerSchluesselException ex) {
            ViewHelper.showError(ex.toString());
        }
        showKontakte(adressbuch.getAlleKontakte());
    }

    public String getKey(Kontakt key) {
        if (key.getName().length() == 0) {
            return key.getTelefon();
        }
        else {
            return key.getName();
        }
    }

    private void inItSearch() {
        searchField.setPromptText(prompter);
        searchField.textProperty().addListener((e) -> filterList());
    }
}
