package adressbuch;

import java.io.Serializable;

public class Kontakt implements Comparable<Kontakt>, Serializable {

    private String name;
    private String telefon;
    private String email;

    public Kontakt(String name, String telefon, String email) {
        // Leere Strings verwenden, wenn einer der Parameter null ist.
        if (name == null) {
            name = "";
        }
        if (telefon == null) {
            telefon = "";
        }
        if (email == null) {
            email = "";
        }
        this.name = name.trim();
        this.telefon = telefon.trim();
        this.email = email.trim();
        if (this.name.equals("") && this.telefon.equals("")) {
            ViewHelper.showError("Name und Telefonnummer dürfen nicht beide leer sein.");
        }
    }

    String gibKontaktString() {
        return name + ":" + telefon + ":" + email;
    }

    public String getName() {
        return name;
    }

    public void setName(String neu) {
        if (telefon.equals("") && neu.trim().equals("")) {
            ViewHelper.showError("Name und Telefonnummer dürfen nicht beide leer sein.");
             return;
        }

        this.name = neu.trim();
    }

    /**
     * @return die Telefonnummer.
     */
    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String neu) {
        if (name.equals("") && neu.trim().equals("")) {
             ViewHelper.showError("Name und Telefonnummer dürfen nicht beide leer sein.");
             return;
        }
        this.telefon = neu.trim();
    }

    /**
     * @return die Adresse.
     */
    public String getEmail() {
        return email;
    }

    public void setEmail(String neu) {
        this.email = neu;
    }

    public boolean equals(Object jenes) {
        if (this == jenes) {
            return true;
        }
        if (jenes == null) {
            return false;
        }
        if (!(jenes instanceof Kontakt)) {
            return false;
        }
        Kontakt jenerKontakt = (Kontakt) jenes;
        return compareTo(jenerKontakt) == 0;
    }

    public String toString() {
        //return name + "\n" + telefon + "\n" + email;
        return name + " -- " + telefon + " -- " + email;
    }

    /**
     * Vergleiche diesen Kontakt mit einem anderen, damit sortiert werden kann.
     * Kontakte werden nach Name, Telefonnummer und Adresse sortiert.
     *
     * @param jenerKontakt der Kontakt, mit dem verglichen werden soll.
     * @return einen negativen Wert, wenn dieser Kontakt vor dem Parameter
     * liegt, Null, wenn sie gleich sind, und einen positiven Wert, wenn dieser
     * Kontakt nach dem Parameter folgt.
     */
    public int compareTo(Kontakt jenerKontakt) {
        int vergleich = name.compareTo(jenerKontakt.getName());
        if (vergleich == 0) {
            vergleich = telefon.compareTo(jenerKontakt.getTelefon());
        }
        if (vergleich == 0) {
            vergleich = email.compareTo(jenerKontakt.getEmail());
        }
        return vergleich;
    }

    public int hashCode() {
        int code = 17;
        code = 37 * code + name.hashCode();
        code = 37 * code + telefon.hashCode();
        code = 37 * code + email.hashCode();
        return code;
    }

}
