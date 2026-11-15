package fr.mm.formation.location.securite;

import java.time.Instant;
import java.util.List;

import fr.mm.formation.location.contrat.modele.Jeton;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
public class JetonService {

    public static final String EMETTEUR = "location-api";

    private final JwtEncoder encodeur;
    private final SecuriteProprietes proprietes;
    private final JetonsRafraichissement jetonsRafraichissement;
    private final UserDetailsService comptes;

    public JetonService(JwtEncoder encodeur, SecuriteProprietes proprietes,
                        JetonsRafraichissement jetonsRafraichissement, UserDetailsService comptes) {
        this.encodeur = encodeur;
        this.proprietes = proprietes;
        this.jetonsRafraichissement = jetonsRafraichissement;
        this.comptes = comptes;
    }

    public Jeton emettre(Authentication authentification) {
        Instant maintenant = Instant.now();
        List<String> roles = authentification.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(autorite -> autorite.replaceFirst("^ROLE_", ""))
                .toList();
        JwtClaimsSet revendications = JwtClaimsSet.builder()
                .issuer(EMETTEUR)
                .subject(authentification.getName())
                .issuedAt(maintenant)
                .expiresAt(maintenant.plus(proprietes.dureeJeton()))
                .claim("roles", roles)
                .build();
        JwsHeader entete = JwsHeader.with(MacAlgorithm.HS256).type("JWT").build();
        String valeur = encodeur.encode(JwtEncoderParameters.from(entete, revendications)).getTokenValue();

        Jeton jeton = new Jeton();
        jeton.setJetonAcces(valeur);
        jeton.setJetonRafraichissement(jetonsRafraichissement.emettre(authentification.getName()));
        jeton.setExpireDans((int) proprietes.dureeJeton().toSeconds());
        jeton.setRoles(roles);
        return jeton;
    }

    public Jeton rafraichir(String jetonRafraichissement) {
        String identifiant = jetonsRafraichissement.consommer(jetonRafraichissement)
                .orElseThrow(RafraichissementRefuseException::new);
        UserDetails compte;
        try {
            compte = comptes.loadUserByUsername(identifiant);
        } catch (UsernameNotFoundException exception) {
            throw new RafraichissementRefuseException();
        }
        return emettre(UsernamePasswordAuthenticationToken.authenticated(
                compte.getUsername(), null, compte.getAuthorities()));
    }
}