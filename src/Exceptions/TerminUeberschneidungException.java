package Exceptions;

import Exceptions.UngueltigerTerminException;
import terminplaner.Termin;

/**
 * Zeigt eine Terminueberschneidung an.
 * @author beuth
 */
public class TerminUeberschneidungException extends UngueltigerTerminException {
    
    public TerminUeberschneidungException(Termin termin){
        super(termin);
    }
    
    public String toString(){
        return String.format("Es gibt bereits einen Termin am %s von %s bis %s%n", termin.getDatum(), termin.getVon(), termin.getBis());
    }
}
