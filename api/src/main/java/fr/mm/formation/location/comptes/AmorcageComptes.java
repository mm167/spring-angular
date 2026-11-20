package fr.mm.formation.location.comptes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AmorcageComptes implements ApplicationRunner {

    private static final Logger JOURNAL = LoggerFactory.getLogger(AmorcageComptes.class);

    private final CompteRepository comptes;
    private final PasswordEncoder encodeur;

    public AmorcageComptes(CompteRepository comptes, PasswordEncoder encodeur) {
        this.comptes = comptes;
        this.encodeur = encodeur;
    }

    @Override
    public void run(ApplicationArguments arguments) {
        if (comptes.count() == 0) {
            comptes.save(new Compte("durand", encodeur.encode("durand123"), "CLIENT"));
            comptes.save(new Compte("gestion", encodeur.encode("gestion123"), "GESTIONNAIRE"));
            JOURNAL.info("Comptes créés : durand, gestion");
        }
    }
}