package fr.mm.formation.location.securite;

public class RafraichissementRefuseException extends RuntimeException {

    public RafraichissementRefuseException() {
        super("Jeton de rafraîchissement invalide ou expiré.");
    }
}