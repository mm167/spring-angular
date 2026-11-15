package fr.mm.formation.location.securite;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class JetonsRafraichissement {

    private record Entree(String identifiant, Instant expiration) {
    }

    private final ConcurrentHashMap<String, Entree> jetons = new ConcurrentHashMap<>();
    private final SecureRandom hasard = new SecureRandom();
    private final SecuriteProprietes proprietes;

    public JetonsRafraichissement(SecuriteProprietes proprietes) {
        this.proprietes = proprietes;
    }

    public String emettre(String identifiant) {
        byte[] octets = new byte[32];
        hasard.nextBytes(octets);
        String jeton = Base64.getUrlEncoder().withoutPadding().encodeToString(octets);
        jetons.put(jeton, new Entree(identifiant, Instant.now().plus(proprietes.dureeRafraichissement())));
        return jeton;
    }

    public Optional<String> consommer(String jeton) {
        Entree entree = jetons.remove(jeton);
        if (entree == null || entree.expiration().isBefore(Instant.now())) {
            return Optional.empty();
        }
        return Optional.of(entree.identifiant());
    }
}