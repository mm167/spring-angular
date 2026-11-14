package fr.mm.formation.location.securite;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class ComptesConfig {

    @Bean
    PasswordEncoder encodeurDeMotsDePasse() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    UserDetailsService comptes(PasswordEncoder encodeur) {
        return new InMemoryUserDetailsManager(
                User.withUsername("durand")
                        .password(encodeur.encode("durand123"))
                        .roles("CLIENT")
                        .build(),
                User.withUsername("gestion")
                        .password(encodeur.encode("gestion123"))
                        .roles("GESTIONNAIRE")
                        .build());
    }

    @Bean
    AuthenticationManager gestionnaireAuthentification(UserDetailsService comptes, PasswordEncoder encodeur) {
        DaoAuthenticationProvider fournisseur = new DaoAuthenticationProvider(comptes);
        fournisseur.setPasswordEncoder(encodeur);
        return new ProviderManager(fournisseur);
    }
}