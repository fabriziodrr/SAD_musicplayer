package it.unisa.musicplayer.servizi;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

public class GestoreOperazioni {

    private static final int LIMITE_OPERAZIONI = 10;

    private final Deque<Operazione> operazioni = new ArrayDeque<>();

    public void eseguiOperazione(Operazione operazione) {
        Objects.requireNonNull(operazione, "L'operazione non può essere null");

        operazione.esegui();
        operazioni.addLast(operazione);

        // Mantiene solo gli ultimi 10 comandi annullabili.
        if (operazioni.size() > LIMITE_OPERAZIONI) {
            operazioni.removeFirst();
        }
    }

    public void annullaUltimaOperazione() {
        if (operazioni.isEmpty()) {
            return;
        }

        Operazione operazione = operazioni.removeLast();
        operazione.annulla();
    }

    public boolean puoAnnullare() {
        return !operazioni.isEmpty();
    }

    public String getDescrizioneUltimaOperazione() {
        return operazioni.isEmpty() ? "" : operazioni.peekLast().getDescrizione();
    }

    public int getNumeroOperazioni() {
        return operazioni.size();
    }
}
