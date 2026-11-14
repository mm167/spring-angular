package fr.mm.formation.location.erreurs;

public class ReservationIntrouvableException extends RuntimeException {

    public ReservationIntrouvableException(Long id) {
        super("Réservation " + id + " introuvable.");
    }
}