package terminplaner;

import Exceptions.TerminUeberschneidungException;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Verwaltet Termine und erlaubt den Zugriff nach Id, Datum oder Text des
 * Termins
 *
 * @author beuth
 */
public class TerminVerwaltung implements Serializable{

    private TreeMap<LocalDate, ArrayList<Termin>> termineDate;
    private HashMap<String, Termin> termineId;

    public TerminVerwaltung() {
        initialisieren();
    }

    protected void initialisieren() {
        termineDate = new TreeMap<LocalDate, ArrayList<Termin>>();
        termineId = new HashMap<String, Termin>();
    }

    /**
     * Liefert die Liste mit allen Terminen des angegebenen Datums.
     *
     * @param date Datum fuer das die Termine abgefragt werden.
     * @return Liste mit Terminen des angegebenen Datums.
     */
    public List<Termin> getTermineTag(LocalDate date) {
        ArrayList<Termin> liste = termineDate.get(date);
        if (liste != null) {
            TreeSet<Termin> set = new TreeSet<Termin>(liste);
            liste = new ArrayList<Termin>(set);
        }
        return liste;
    }

    /**
     * Fuegt den angegebenen Termin in die Terminverwaltung ein, wenn keine
     * Terminueberschneidung mit anderen Terminen vorliegt.
     *
     * @param t Hinzuzufuegender Termin
     * @throws TerminUeberschneidungException wenn es bereits einen Termin gibt,
     * der den angegebenen Zeitraum schneidet.
     */
    public void addTermin(Termin t) throws TerminUeberschneidungException {
        Termin alt = checkTerminUeberschneidung(t);
        if (alt == null) {
            addTerminDate(t);
            termineId.put(t.getId(), t);
        } else {
            System.out.println("Beim Hinzufuegen wurde ein Termin gefunden: " + alt.toString());
            throw new TerminUeberschneidungException(alt);
        }
    }

    /**
     * Fuegt den angegebenen Termin in die TreeMap termineDate ein. Falls noch
     * keine Liste zu dem Datum eingetragen ist, wird eine neue ArrayList
     * erstellt.
     *
     * @param t Einzufuegender Termin
     */
    private void addTerminDate(Termin t) {
        ArrayList termine = termineDate.get(t.getDatum());
        if (termine == null) {
            termine = new ArrayList<Termin>();
            termineDate.put(t.getDatum(), termine);
        }
        termine.add(t);
    }

    /**
     * Aktualisiert den uebergebenen Termin in der Terminverwaltung. Dazu wird
     * zunächst der alte Termin mit der Id gelöscht und dann der neue Termin
     * hinzugefuegt. Sollte dies fehlschlagen, wid der alte Termin
     * wiederhergestellt.
     *
     * @param neu zu aktualisierender Termin
     * @return true, wenn das Update funktioniert hat, false sonst.
     * @throws TerminUeberschneidungException wenn es eine Ueberschneidung mit
     * dem neuen gibt.
     */
    public boolean updateTermin(Termin neu) throws TerminUeberschneidungException {
        Termin alt;
        if (neu == null) {
            return false;
        }
        alt = termineId.get(neu.getId());
        if (alt != null) {
            removeTermin(alt);
            try {
                addTermin(neu);
            } catch (TerminUeberschneidungException e) {
                // Wenn der Ueberschneidungstermin dieselbe ID hat, dann wird kein Fehler geworfen.
                System.out.println("Update Termin in Terminverwaltung: ID Speicher: " + e.getTermin().getId() + " ID neu: " + neu.getId());
                if (!e.getTermin().getId().equals(neu.getId())) {
                    restoreTermin(alt);
                    throw e;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Loescht den Termin aus allen Listen.
     *
     * @param t zu loeschender Termin.
     */
    public void removeTermin(Termin t) {
        termineId.remove(t.getId());
        ArrayList<Termin> liste = termineDate.get(t.getDatum());
        liste.remove(t);
        if (liste.isEmpty()) {
            termineDate.remove(t.getDatum());
        }
    }

    /**
     * Fuegt einen geloeschten Termin wieder in die Listen ein. Hierbei wird
     * nicht geprueft, ob es Ueberschneidungen gibt. Ein eventuell noch
     * vorhandener Termin wird ueberschrieben.
     *
     * @param t zu speichernder Termin.
     */
    private void restoreTermin(Termin t) {
        addTerminDate(t);
        termineId.put(t.getId(), t);
    }

    /**
     * Prueft, ob sich der uebergebene Termin mit einem Termin der
     * Terminverwaltung schneidet.
     *
     * @param neu zu pruefender Termin.
     * @return Termin, mit dem eine Ueberschneidung besteht oder null.
     */
    public Termin checkTerminUeberschneidung(Termin neu) {
        List<Termin> liste = termineDate.get(neu.getDatum());
        if (liste != null) {
            for (Termin alt : liste) {
                if (alt.isUeberschneidung(neu)) {
                    return alt;
                }
            }
        }
        return null;
    }

    /**
     * Liefert den Termin mit der angegebenen id
     *
     * @param id id des gewuenschten Termins.
     * @return Termin zu der id oder null
     */
    public Termin getTermin(String id) {
        return termineId.get(id);
    }

    /**
     * Liefert alle Termine im Planer.
     *
     * @return alle Termine als Array.
     */
    public Termin[] getAllTermine() {
        Termin[] termine = new Termin[termineId.size()];
        termineId.values().toArray(termine);
        return termine;
    }
}
