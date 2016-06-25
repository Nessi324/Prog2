package terminplaner;

import Exceptions.UngueltigerTerminException;
import adressbuch.Kontakt;
import adressbuch.ViewHelper;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Die Klasse Termin repraesentiert einen Termin, an 
 * einem bestimmten Datum stattfindet, der eine bestimmte Zeitdauer besitzt, 
 * mit einem Text versehen sein kann, von einem Nutzer angelegt wurde und
 * fuer den eine Mengen an Teilnehmern angegeben werden kann.
 * @author beuth
 */
public class Termin implements Comparable<Termin>, Serializable {

    private String tid;
    private String text;
    private LocalDate datum;
    private LocalTime von;
    private LocalTime bis;
    private Kontakt besitzer;
    private TreeSet<Kontakt> teilnehmer;

    public Termin(String text, LocalDate datum, LocalTime von, LocalTime bis) throws UngueltigerTerminException {
        tid = text + datum + von + bis;
        this.text = text;
        this.datum = datum;
        this.von = von;
        this.bis = bis;
        this.besitzer = null;
        teilnehmer = new TreeSet<Kontakt>();
        // Wenn das von-Datum großer ist als das bis-Datum, dann ist der Termin ungültig
        if (von.compareTo(bis) >= 0) {
            throw new UngueltigerTerminException(this);
        }
    }
    
    /**
     * Erzeugt eine Kopie des Objekts this und liefert die Kopie zurueck.
     * Diese Methode wird bei einem Update benötigt, um eine Kopie des Termins 
     * zu erstellen, bei dem bestimmte Infos geändert werden. Dieser Termin hat insbesondere
     * die gleiche id und den gleichen Besitzer.
     * @return Kopie des Objekts auf dem die Methode aufgerufen wurde.
     */
    public Termin getCopy() {
        try {
            Termin kopie = new Termin(this.getText(), this.getDatum(), this.getVon(), this.getBis());
            kopie.tid = this.tid;
            kopie.besitzer = this.besitzer;
            return kopie;
        } catch (UngueltigerTerminException ex) {
            Logger.getLogger(Termin.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String getText() {
        return text;
    }

    public LocalDate getDatum() {
        return datum;
    }

    public LocalTime getVon() {
        return von;
    }

    public LocalTime getBis() {
        return bis;
    }

    public Kontakt getBesitzer() {
        return besitzer;
    }

    public TreeSet<Kontakt> getTeilnehmer() {
        return teilnehmer;
    }

    public long getDauer() {
        long dauer = 0;
        dauer = von.until(bis, ChronoUnit.MINUTES);
        return dauer;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setBesitzer(Kontakt besitzer) {
        this.besitzer = besitzer;
    }

    public void setDatum(LocalDate datum) {
        this.datum = datum;
    }

    /**
     * Setzt den Zeitraum des Termins.
     * @param von Startzeitpunkt
     * @param bis Endezeitpunkt
     * @throws UngueltigerTerminException wenn der Endezeitpunkt vor dem Startzeitpunkt liegt.
     */
    public void setVonBis(LocalTime von, LocalTime bis) throws UngueltigerTerminException {
        if (von.compareTo(bis) >= 0) {
            throw new UngueltigerTerminException(this);
        }
        this.von = von;
        this.bis = bis;
    }

   public void addTeilnehmer(Kontakt teiln) {
            this.teilnehmer.add(teiln);
    }

    public String getId() {
        return tid;
    }
    
    /**
     * Prueft, ob sich der aktuelle Termin mit derm Termin t ueberschneidet
     * @param t Termin, gegen den geprueft werden soll.
     * @return true, wenn die beiden Termine sich ueberschneiden, false sonst.
     */
    public boolean isUeberschneidung(Termin t) {
        if (!this.getDatum().equals(t.getDatum())) {
            return false;
        }
        // Keine Ueberschneidung, wenn Start und Ende von t vor this liegen
        if(t.getVon().compareTo(von) < 0 && t.getBis().compareTo(von) <= 0) return false;
        // Keine Ueberschneidung, wenn Start und Ende von t nach this liegen
        else if(t.getVon().compareTo(bis) >= 0 ) return false;
        // Alle anderen Fälle haben eine Ueberschneidung
        else return true;
        }


    /**
     * Vergleiche diesen Termin mit einem anderen, damit sortiert werden kann.
     * Termine werden nach Datum und von bis, sortiert.
     *
     * @param jenerTermin der Termin, mit dem verglichen werden soll.
     * @return einen negativen Wert, wenn dieser Termin vor dem Parameter liegt,
     * Null, wenn sie gleich sind, und einen positiven Wert, wenn dieser Kontakt
     * nach dem Parameter folgt.
     */
    public int compareTo(Termin jenerTermin) {
        int vergleich = datum.compareTo(jenerTermin.getDatum());
        if (vergleich == 0) {
            vergleich = von.compareTo(jenerTermin.getVon());
        }
        if (vergleich == 0) {
            vergleich = bis.compareTo(jenerTermin.getBis());
        }
        return vergleich;
    }

    /**
     * Pruefe dieses und jenes Objekt auf Datengleichheit.
     *
     * @param jenes Das Objekt, das mit diesem verglichen werden soll.
     * @return true wenn das Parameterobjekt ein Termin ist und sich die
     * Datenfelder paarweise gleichen.
     */
    public boolean equals(Object jenes) {
        if (this == jenes) {
            return true;
        }
        if (jenes == null) {
            return false;
        }
        if (!(jenes instanceof Termin)) {
            return false;
        }
        Termin jenerTermin = (Termin) jenes;
        return compareTo(jenerTermin) == 0;
    }

    public String toString() {
        String termin = "";
        termin = von + " - " + bis + " : " + text;
        return termin;
    }
    
    /**
     * Erzeugt einen String mit den ausfuehrlichen Infos des Termins.
     * @return String mit den Infos zum Termin.
     */
    public String getInfo() {
        String text = "Datum: " + datum + "\n";
        text = text + toString() + "\n";
        text = text + "Teilnehmer: " + getTeilnehmerInfo(); 
        return text;
    }
    
    /**
     * Erzeugt einen String mit der Auflistung der Teilnehmer.
     * @return String mit den Infos zu den Teilnehmern.
     */
    private String getTeilnehmerInfo() {
        String text = "";
        int last = teilnehmer.size() - 1;
        int i = 0;
        for(Kontakt k : teilnehmer) {
            text = text + k.getName();
            if(i < last) text = text + ", ";
            i++;
        }
        return text;
    }
}
