package fr.mm.formation.location.securite;

import java.time.Instant;
import java.util.List;

import fr.mm.formation.location.contrat.modele.Jeton;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
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

    public JetonService(JwtEncoder encodeur, SecuriteProprietes proprietes) {
        this.encodeur = encodeur;
        this.proprietes = proprietes;
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
        jeton.setExpireDans((int) proprietes.dureeJeton().toSeconds());
        jeton.setRoles(roles);
        return jeton;
    }
}