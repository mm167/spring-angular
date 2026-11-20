package fr.mm.formation.location.comptes;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ComptesService implements UserDetailsService {

    private final CompteRepository comptes;

    public ComptesService(CompteRepository comptes) {
        this.comptes = comptes;
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
}