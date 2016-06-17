package Exceptions;

import terminplaner.Termin;

/**
 * Zeigt einen ungueltigen Termin an.
 * @author beuth
 */
public class UngueltigerTerminException extends Exception {
    protected Termin termin;
    
    public UngueltigerTerminException(Termin t) {
        termin = t;
    }
    
    public Termin getTermin() {
        return termin;
    }
    
    public String toString(){
        return String.format("Das Startdatum muss vor dem Ende-Datum liegen!");
    }
}
