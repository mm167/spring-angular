package fr.mm.formation.location.erreurs;

public class ChevauchementException extends RuntimeException {

    public ChevauchementException(Long materielId) {
        super("Le matériel " + materielId + " est déjà réservé sur cette période.");
    }
}