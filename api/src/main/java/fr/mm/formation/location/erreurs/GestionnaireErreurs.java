package fr.mm.formation.location.erreurs;

import java.util.stream.Collectors;

import fr.mm.formation.location.contrat.modele.Erreur;
import fr.mm.formation.location.securite.RafraichissementRefuseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GestionnaireErreurs {

    @ExceptionHandler({MaterielIntrouvableException.class, ReservationIntrouvableException.class})
    public ResponseEntity<Erreur> ressourceIntrouvable(RuntimeException exception) {
        return reponse(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(ChevauchementException.class)
    public ResponseEntity<Erreur> periodeDejaReservee(ChevauchementException exception) {
        return reponse(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Erreur> contenuInvalide(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(erreur -> erreur.getField() + " : " + erreur.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return reponse(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Erreur> corpsIllisible(HttpMessageNotReadableException exception) {
        return reponse(HttpStatus.BAD_REQUEST, "Corps de la requête illisible ou invalide.");
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Erreur> identifiantsIncorrects(BadCredentialsException exception) {
        return reponse(HttpStatus.UNAUTHORIZED, "Identifiant ou mot de passe incorrect.");
    }

    @ExceptionHandler(RafraichissementRefuseException.class)
    public ResponseEntity<Erreur> rafraichissementRefuse(RafraichissementRefuseException exception) {
        return reponse(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    private ResponseEntity<Erreur> reponse(HttpStatus statut, String message) {
        Erreur erreur = new Erreur();
        erreur.setStatus(statut.value());
        erreur.setMessage(message);
        return ResponseEntity.status(statut).body(erreur);
    }
}