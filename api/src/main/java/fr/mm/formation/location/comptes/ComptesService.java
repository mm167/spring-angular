package fr.mm.formation.location.comptes;

import fr.mm.formation.location.erreurs.IdentifiantDejaPrisException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ComptesService implements UserDetailsService {

    private static final String ROLE_INSCRIPTION = "CLIENT";

    private final CompteRepository comptes;
    private final PasswordEncoder encodeur;

    public ComptesService(CompteRepository comptes, PasswordEncoder encodeur) {
        this.comptes = comptes;
        this.encodeur = encodeur;
    }

    @Override
    public UserDetails loadUserByUsername(String identifiant) {
        Compte compte = comptes.findByIdentifiant(identifiant)
                .orElseThrow(() -> new UsernameNotFoundException("Compte inconnu : " + identifiant));
        return User.withUsername(compte.getIdentifiant())
                .password(compte.getMotDePasse())
                .roles(compte.getRole())
                .build();
    }

    public Compte inscrire(String identifiant, String motDePasse) {
        if (comptes.existsByIdentifiant(identifiant)) {
            throw new IdentifiantDejaPrisException(identifiant);
        }
        try {
            return comptes.save(new Compte(identifiant, encodeur.encode(motDePasse), ROLE_INSCRIPTION));
        } catch (DataIntegrityViolationException exception) {
            throw new IdentifiantDejaPrisException(identifiant);
        }
    }
}