package fr.mm.formation.location.erreurs;

public class MaterielIntrouvableException extends RuntimeException {

    public MaterielIntrouvableException(Long id) {
        super("Matériel " + id + " introuvable.");
    }
}