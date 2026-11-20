package fr.mm.formation.location.rest;

import fr.mm.formation.location.comptes.Compte;
import fr.mm.formation.location.comptes.ComptesService;
import fr.mm.formation.location.contrat.api.AuthentificationApi;
import fr.mm.formation.location.contrat.modele.CompteCree;
import fr.mm.formation.location.contrat.modele.DemandeRafraichissement;
import fr.mm.formation.location.contrat.modele.Identifiants;
import fr.mm.formation.location.contrat.modele.Inscription;
import fr.mm.formation.location.contrat.modele.Jeton;
import fr.mm.formation.location.securite.JetonService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthentificationController implements AuthentificationApi {

    private final AuthenticationManager gestionnaire;
    private final JetonService jetons;
    private final ComptesService comptes;

    public AuthentificationController(AuthenticationManager gestionnaire, JetonService jetons,
                                      ComptesService comptes) {
        this.gestionnaire = gestionnaire;
        this.jetons = jetons;
        this.comptes = comptes;
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

    @Override
    public ResponseEntity<CompteCree> inscrire(Inscription inscription) {
        Compte compte = comptes.inscrire(inscription.getIdentifiant(), inscription.getMotDePasse());
        CompteCree reponse = new CompteCree();
        reponse.setIdentifiant(compte.getIdentifiant());
        reponse.setRole(compte.getRole());
        return ResponseEntity.status(HttpStatus.CREATED).body(reponse);
    }
}