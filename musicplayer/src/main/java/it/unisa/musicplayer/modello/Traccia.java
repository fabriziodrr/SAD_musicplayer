package it.unisa.musicplayer.modello;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

public class Traccia {

    private final String id;
    private String titolo;
    private String autore;
    private String durata;
    private String genere;
    private int    anno;
    private final Set<Tag> tag;
    @JsonIgnore private int contaRiproduzioni;

    @JsonCreator
    public Traccia(@JsonProperty("id") String id,
                   @JsonProperty("titolo") String titolo,
                   @JsonProperty("autore") String autore,
                   @JsonProperty("durata") String durata,
                   @JsonProperty("genere") String genere,
                   @JsonProperty("anno") int anno) {

        validaStringa(id,     "id");
        validaStringa(titolo, "titolo");
        validaStringa(autore, "autore");
        validaStringa(genere, "genere");
        validaDurata(durata);
        validaAnno(anno);

        this.id                = id.trim();
        this.titolo            = titolo.trim();
        this.autore            = autore.trim();
        this.durata            = durata.trim();
        this.genere            = genere.trim();
        this.anno              = anno;
        this.tag             = EnumSet.noneOf(Tag.class);
        this.contaRiproduzioni = 0;
    }

    //Getter

    public String getId()     { return id; }
    public String getTitolo() { return titolo; }
    public String getAutore() { return autore; }
    public String getDurata() { return durata; }
    public String getGenere() { return genere; }
    public int    getAnno()   { return anno; }
    @JsonIgnore public int    getContaRiproduzioni() { return contaRiproduzioni; }

    
    //Restituisce una copia difensiva del set di tag per evitare modifiche esterne non controllate.
    public Set<Tag> getTag() {
        return EnumSet.copyOf(tag.isEmpty() ? EnumSet.noneOf(Tag.class) : tag);
    }

    //Setter(con validazione) 
    public void setTitolo(String titolo) {
        validaStringa(titolo, "titolo");
        this.titolo = titolo.trim();
    }

    public void setAutore(String autore) {
        validaStringa(autore, "autore");
        this.autore = autore.trim();
    }

    public void setDurata(String durata) {
        validaDurata(durata);
        this.durata = durata.trim();
    }

    public void setGenere(String genere) {
        validaStringa(genere, "genere");
        this.genere = genere.trim();
    }

    public void setAnno(int anno) {
        validaAnno(anno);
        this.anno = anno;
    }

    // Gestione Tag 
    // Aggiunge un tag visivo alla traccia. Operazione idempotente:aggiungere un tag già presente non ha effetti.
    public void aggiungiTag(Tag tag) {
        Objects.requireNonNull(tag, "Il tag non può essere null");
        this.tag.add(tag);
    }

    
    //Rimuove un tag visivo dalla traccia. Operazione idempotente:rimuovere un tag non presente non ha effetti.
    public void rimuoviTag(Tag tag) {
        Objects.requireNonNull(tag, "Il tag non può essere null");
        this.tag.remove(tag);
    }

    //Restituisce true se la traccia ha il tag specificato.
    public boolean hasTag(Tag tag) {
        Objects.requireNonNull(tag, "Il tag non può essere null");
        return this.tag.contains(tag);
    }

    //Riproduzione 


    //Incrementa il contatore delle riproduzioni: chiamato da Lettore ogni volta che la traccia viene riprodotta.
    public void incrementaRiproduzioni() {
        contaRiproduzioni++;
    }

    

    /*
      Due tracce sono uguali se e solo se hanno lo stesso id.
      Necessario per il corretto funzionamento in Set e Map.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Traccia)) return false;
        Traccia altra = (Traccia) obj;
        return id.equals(altra.id);
    }

    
    //Consistente con equals: basato solo sull'id.
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /*
     Ordinamento naturale per titolo (case-insensitive),
     con autore come criterio di spareggio.
     Usato da Collections.sort() e TreeSet senza Comparator esplicito.
     */
    public int compareTo(Traccia altra) {
        int cmp = this.titolo.compareToIgnoreCase(altra.titolo);
        if (cmp != 0) return cmp;
        return this.autore.compareToIgnoreCase(altra.autore);
    }

    @Override
    public String toString() {
        return String.format("Traccia{id='%s', titolo='%s', autore='%s', " +
                             "durata='%s', genere='%s', anno=%d, tags=%s}",
                             id, titolo, autore, durata, genere, anno, tag);
    }

    //Validazione(privata) 

    private static void validaStringa(String valore, String nomeCampo) {
        if (valore == null || valore.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Il campo '" + nomeCampo + "' non può essere null o vuoto");
        }
    }

    /*
     La durata deve rispettare il formato mm:ss dove mm >= 0 e 0 <= ss <= 59.
     Esempi validi: "3:45", "0:30", "12:00"
     */
    private static void validaDurata(String durata) {
        if (durata == null || durata.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "La durata non può essere null o vuota");
        }
        if (!durata.trim().matches("\\d+:[0-5]\\d")) {
            throw new IllegalArgumentException(
                "La durata deve essere nel formato mm:ss (es. '3:45'). " +
                "Valore ricevuto: '" + durata + "'");
        }
    }

    private static void validaAnno(int anno) {
        if (anno <= 0) {
            throw new IllegalArgumentException(
                "L'anno deve essere un valore positivo. Valore ricevuto: " + anno);
        }
    }
}
