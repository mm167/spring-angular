package fr.mm.formation.location.erreurs;

public class IdentifiantDejaPrisException extends RuntimeException {

    public IdentifiantDejaPrisException(String identifiant) {
        super("L'identifiant " + identifiant + " est déjà utilisé.");
    }
}
