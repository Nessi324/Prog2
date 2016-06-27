package terminplaner;

import Exceptions.TerminUeberschneidungException;
import Exceptions.UngueltigerTerminException;
import adressbuch.Kontakt;
import adressbuch.ViewHelper;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Repraesentiert einen Terminplaner mit einem Besitzer, der Termine ueber den
 * Server an alle angemeldeten Teilnehmer schicken kann.
 *
 * @author beuth
 */
public class Terminplaner extends TerminVerwaltung {

    private Kontakt besitzer;

    /**
     * Initialisiert den Terminplaner. Hier wird der Client erzeugt und der
     * Besitzer gesetzt.
     *
     * @param k Besitzer des Planers
     */
    public Terminplaner(Kontakt k) {
        besitzer = k;
        setTestData();
    }

    public Kontakt getBesitzer() {
        return besitzer;
    }

    public void setBesitzer(Kontakt besitzer) {
        this.besitzer = besitzer;
    }

    /**
     * Darf der Termin von dem Besitzer geaendert werden. Ist nur erlaubt, wenn
     * der Besitzer des Terminplaners auch der Besitzer des Termins ist.
     *
     * @param t Termin, der geprueft werden soll.
     * @return true, wenn er geaendert werden darf, false sonst.
     */
    public boolean updateErlaubt(Termin t) {
        if (t.getBesitzer() == null) {
            return true;
        }
        else {
            return besitzer.equals(t.getBesitzer());
        }

    }

    /**
     * Fuegt dem Planer einen neuen Termin hinzu. Sollte noch kein Besitzer
     * eingetrage sein, so wird der Besitzer des Planers gesetzt.
     *
     * @param t neuer Termin.
     * @throws TerminUeberschneidungException
     */
    public void newTermin(Termin t) throws TerminUeberschneidungException {
        if (t.getBesitzer() == null) {
            t.setBesitzer(besitzer);
        }
        addTermin(t);
    }

    /**
     * Ein neuer/editierter/empfangener Termin soll in den Planer eingefuegt
     * werden. Handelt es sich um einen Termin ohne Besitzer oder mit einem
     * anderen Besitzer als dem des Planers, so wird er als neuer Termin
     * hinzugefuegt. Ansonsten muss der Termin im Planer aktualisiert werden.
     *
     * @param neu einzufuegender Termin.
     * @throws TerminUeberschneidungException
     */
    public void setTermin(Termin neu) throws TerminUeberschneidungException {
        if (neu != null && !besitzer.equals(neu.getBesitzer())) {
            newTermin(neu);
        }
        else {
            updateTermin(neu);
        }
    }

    public void save(File file) throws IOException {
            if (getAllTermine() != null && getAllTermine().length > 0 && file != null) {
                Termin[] termine = getAllTermine();
                FileOutputStream f = new FileOutputStream(file);
                ObjectOutputStream os = new ObjectOutputStream(f);
                os.writeObject(termine);
                os.close();
            }
    }

    public void load(File file) throws Exception {
        if (file != null) {
            FileInputStream fileInput = new FileInputStream(file);
            ObjectInputStream os = new ObjectInputStream(fileInput);
            initialisieren();
            Termin[] termin = (Termin[]) os.readObject();
            for (Termin x : termin) {
                if (x != null) {
                    addTermin(x);
                }
                os.close();
            }
        }
    }

    private void setTestData() {
        LocalDate d = LocalDate.of(2016, 06, 27);
        Kontakt dave = new Kontakt("david", "08459 100000", "david@gmx.de");
        try {
            Termin t1 = new Termin("Besuch", d, LocalTime.of(9, 0), LocalTime.of(10, 0));
            Termin t2 = new Termin("Brunch", d, LocalTime.of(10, 30), LocalTime.of(11, 0));
            Termin t3 = new Termin("Freizeit", d, LocalTime.of(11, 15), LocalTime.of(12, 30));
            Termin t4 = new Termin("Sport", d, LocalTime.of(13, 15), LocalTime.of(14, 30));
            Termin t5 = new Termin("Essen", d, LocalTime.of(14, 30), LocalTime.of(15, 30));
            Termin t6 = new Termin("Arbeiten", d, LocalTime.of(16, 15), LocalTime.of(17, 30));
            t1.setBesitzer(besitzer);
            t2.setBesitzer(besitzer);
            t3.setBesitzer(besitzer);
            t4.setBesitzer(besitzer);
            t5.setBesitzer(besitzer);
            t6.setBesitzer(dave);
            addTermin(t1);
            addTermin(t2);
            addTermin(t3);
            addTermin(t4);
            addTermin(t5);
            addTermin(t6);
        } catch (UngueltigerTerminException e) {
            System.out.println(e);
        }
    }
}
