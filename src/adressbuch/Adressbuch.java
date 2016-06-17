package adressbuch;

import Exceptions.DoppelterSchluesselException;
import Exceptions.KeinPassenderKontaktException;
import Exceptions.UngueltigerSchluesselException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

public class Adressbuch {

    // Speicher fuer beliebige Anzahl von Kontakten.
    private TreeMap<String, Kontakt> buch;
    private int anzahlEintraege;

    /**
     * Initialisiere ein neues Adressbuch.
     */
    public Adressbuch() {
        buch = new TreeMap<String, Kontakt>();
        anzahlEintraege = 0;
        setTestData();
    }

    /**
     * Schlage einen Namen oder eine Telefonnummer nach und liefere den
     * zugehoerigen Kontakt.
     *
     * @param schluessel der Name oder die Nummer zum Nachschlagen.
     * @return den zum Schluessel gehoerenden Kontakt.
     * @throws adressbuch.UngueltigerSchluesselException wenn der Schluessel
     * ungueltig ist (leerer String oder nur Leerzeichen).
     */
    public Kontakt getKontakt(String schluessel) throws UngueltigerSchluesselException {
        if (schluesselBekannt(schluessel)) {
            return buch.get(schluessel);
        }
        else {
            throw new KeinPassenderKontaktException(schluessel);
        }
    }

    /**
     * Ist der gegebene Schluessel in diesem Adressbuch bekannt?
     *
     * @param schluessel der Name oder die Nummer zum Nachschlagen.
     * @return true wenn der Schluessel eingetragen ist, false sonst.
     * @throws adressbuch.UngueltigerSchluesselException wenn der Schluessel
     * ungueltig ist (leerer String oder nur Leerzeichen).
     */
    public boolean schluesselBekannt(String schluessel) throws UngueltigerSchluesselException {
        if (schluessel.trim().length() == 0) {
            throw new UngueltigerSchluesselException(schluessel);
        }
        if (schluessel == null) {
            throw new IllegalStateException("Das Argument schluessel darf nicht den Wert null haben!");
        }
        return buch.containsKey(schluessel);
    }

    public void addKontakt(Kontakt kontakt) throws DoppelterSchluesselException {

        if (kontakt == null) {
            throw new IllegalArgumentException("Null-Wert in neuerKontakt().");
        }
        try {
            String name = kontakt.getName();
            String telNr = kontakt.getTelefon();
            if (!name.isEmpty() && schluesselBekannt(name)) {
                throw new DoppelterSchluesselException(name);
            }
            if (!telNr.isEmpty() && schluesselBekannt(telNr)) {
                throw new DoppelterSchluesselException(telNr);
            }
        } catch (UngueltigerSchluesselException ex) {
            if (ex instanceof DoppelterSchluesselException) {
                throw (DoppelterSchluesselException) ex;
            }
        }
        buch.put(kontakt.getName(), kontakt);
        buch.put(kontakt.getTelefon(), kontakt);
        anzahlEintraege++;
    }

    /**
     * Aendere die Kontaktdaten des Kontakts, der bisher unter dem gegebenen
     * Schluessel eingetragen war.
     *
     * @param alterSchluessel einer der verwendeten Schl?ssel.
     * @param daten die neuen Kontaktdaten.
     * @throws adressbuch.UngueltigerSchluesselException wenn der Schluessel
     * ungueltig ist (leerer String oder nur Leerzeichen).
     */
    public void updateKontakt(String alterSchluessel, Kontakt daten) throws UngueltigerSchluesselException, KeinPassenderKontaktException {

        Kontakt kontakt = null;
        if (daten == null) {
            throw new IllegalArgumentException("Das Argument kontakt darf nicht den Wert null haben!");
        }
        try {
            if (schluesselBekannt(alterSchluessel)) {
                kontakt = deleteKontakt(alterSchluessel);
                addKontakt(daten);
            }
            else {
                throw new KeinPassenderKontaktException(alterSchluessel);
            }
        } catch (DoppelterSchluesselException ex) {
            addKontakt(kontakt);
            throw ex;
        }
    }

    /**
     * Suche nach allen Kontakten mit einem Schluessel, der mit dem gegebenen
     * Praefix beginnt.
     *
     * @param praefix der Schluesselpraefix, nach dem gesucht werden soll.
     * @return ein Array mit den gefundenen Kontakten.
     */
    public Kontakt[] getKontakte(String praefix) {
        if (praefix == null || praefix.isEmpty()) {
            return getAlleKontakte();
        }
        // Eine Liste fuer die Treffer anlegen.
        List<Kontakt> treffer = new LinkedList<Kontakt>();
        // Finden von Schluesseln, die gleich dem oder groesser als
        // der Praefix sind.
        SortedMap<String, Kontakt> rest = buch.tailMap(praefix);
        Iterator<String> it = rest.keySet().iterator();
        // Stoppen bei der ersten Nichtuebereinstimmung.
        boolean sucheBeendet = false;
        while (!sucheBeendet && it.hasNext()) {
            String schluessel = it.next();
            if (schluessel.startsWith(praefix)) {
                treffer.add(buch.get(schluessel));
            }
            else {
                sucheBeendet = true;
            }
        }
        Kontakt[] ergebnisse = new Kontakt[treffer.size()];
        treffer.toArray(ergebnisse);
        return ergebnisse;
    }

    /**
     * @return die momentane Anzahl der Eintr?ge in diesem Adressbuch.
     */
    public int gibAnzahlEintraege() {
        return anzahlEintraege;
    }

    /**
     * Entferne den Eintrag mit dem gegebenen Schluessel aus diesem Adressbuch.
     *
     * @param schluessel einer der Schluessel des Eintrags, der entfernt werden
     * soll.
     * @return den geloeschten Kontakt oder null
     * @throws adressbuch.UngueltigerSchluesselException wenn der Schluessel
     * ungueltig ist (leerer String oder nur Leerzeichen).
     * @throws adressbuch.KeinPassenderKontaktException wenn fuer den Schluessel
     * kein Kontakt gefunden wurde.
     */
    public Kontakt deleteKontakt(String schluessel) throws UngueltigerSchluesselException, KeinPassenderKontaktException {
        if (schluesselBekannt(schluessel)) {
            Kontakt kontakt = buch.get(schluessel);
            if (kontakt != null) {
                buch.remove(kontakt.getName());
                buch.remove(kontakt.getTelefon());
                anzahlEintraege--;
                return kontakt;
            }
        }
        else {
            throw new KeinPassenderKontaktException(schluessel);
        }
        return null;
    }

    /**
     * @return alle Kontaktdaten, sortiert nach der Sortierreihenfolge, die die
     * Klasse Kontakt definiert.
     */
    public String getAlleAlsText() {
        // Weil jeder Satz unter zwei Schluesseln eingetragen ist,
        // muss ein Set mit den Kontakten gebildet werden. Dadurch
        // werden Duplikate entfernt.
        StringBuffer alleEintraege = new StringBuffer();
        Set<Kontakt> sortierteDaten = new TreeSet<Kontakt>(buch.values());
        for (Kontakt kontakt : sortierteDaten) {
            alleEintraege.append(kontakt);
            alleEintraege.append('\n');
            alleEintraege.append('\n');
        }
        return alleEintraege.toString();
    }

    public Kontakt[] getAlleKontakte() {
        Set<Kontakt> sortierteDaten = new TreeSet<Kontakt>(buch.values());
        Kontakt[] ergebnisse = new Kontakt[sortierteDaten.size()];
        sortierteDaten.toArray(ergebnisse);
        return ergebnisse;
    }

    public void setTestData() {
        Kontakt[] testdaten = {
            new Kontakt("david", "08459 100000", "david@gmx.de"),
            new Kontakt("michael", "08459 200000", "michael@gmx.de"),
            new Kontakt("john", "08459 300000", "john@gmx.de"),
            new Kontakt("heike", "08459 400000", "heike@gmx.de"),
            new Kontakt("emma", "08459 500000", "emma@gmx.de"),
            new Kontakt("simone", "08459 600000", "simone@gmx.de"),
            new Kontakt("chris", "08459 700000", "chris@gmx.de"),
            new Kontakt("merry", "08459 700345", "merry@gmx.de"),
            new Kontakt("pope", "08459 707600", "peope@gmx.de"),
            new Kontakt("jake", "08459 709900", "jake@gmx.de"),
            new Kontakt("cake", "08459 709200", "cake@gmx.de"),
            new Kontakt("butter", "08459 134000", "butter@gmx.de"),
            new Kontakt("stuard", "08459 947000", "stuard@gmx.de"),
            new Kontakt("kenny", "08459 706900", "kennies@gmx.de"),
            new Kontakt("peter", "08459 703760", "peter@gmx.de"),
            new Kontakt("axel", "08459 805800", "axel@gmx.de"),};
        for (Kontakt kontakt : testdaten) {
            try {
                addKontakt(kontakt);
            } catch (DoppelterSchluesselException ex) {
                ViewHelper.showError(ex.toString());
            }
        }
    }
}
