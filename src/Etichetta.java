/**
 * Created by mark on 04/03/17.
 */
public class Etichetta {
    private String descrizione;
    private String prezzo;

    public Etichetta (String descr, String p){
        this.setDescrizione(descr);
        this.setPrezzo(p);
    }

    public String getDescrizione() {
        return descrizione;
    }

    public String getPrezzo() {
        return prezzo;
    }

    public String getPrezzoEuro() {
        prezzo = "€ " + prezzo;
        prezzo = prezzo.replace(".", ",");
        return prezzo;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
    public void setPrezzo(String prezzo) {

        this.prezzo = prezzo;
    }


}
