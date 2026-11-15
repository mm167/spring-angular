package fr.mm.formation.location.rest;

import fr.mm.formation.location.contrat.api.AuthentificationApi;
import fr.mm.formation.location.contrat.modele.Identifiants;
import fr.mm.formation.location.contrat.modele.Jeton;
import fr.mm.formation.location.securite.JetonService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;
import fr.mm.formation.location.contrat.modele.DemandeRafraichissement;

@RestController
public class AuthentificationController implements AuthentificationApi {

    private final AuthenticationManager gestionnaire;
    private final JetonService jetons;

    public AuthentificationController(AuthenticationManager gestionnaire, JetonService jetons) {
        this.gestionnaire = gestionnaire;
        this.jetons = jetons;
    }

    @Override
    public ResponseEntity<Jeton> seConnecter(Identifiants identifiants) {
        Authentication authentification = gestionnaire.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(
                        identifiants.getIdentifiant(), identifiants.getMotDePasse()));
        return ResponseEntity.ok(jetons.emettre(authentification));
    }

    @Override
    public ResponseEntity<Jeton> rafraichir(DemandeRafraichissement demande) {
        return ResponseEntity.ok(jetons.rafraichir(demande.getJetonRafraichissement()));
    }
}