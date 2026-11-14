package fr.mm.formation.location.securite;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecuriteConfig {

    @Bean
    SecurityFilterChain chaineDeSecurite(HttpSecurity http, ReponsesErreurSecurite erreurs) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(autorisations -> autorisations
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/materiels/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/materiels").hasRole("GESTIONNAIRE")
                        .requestMatchers(HttpMethod.DELETE, "/api/reservations/**").hasRole("GESTIONNAIRE")
                        .requestMatchers("/ws/**", "/v3/api-docs/**", "/swagger-ui/**", "/documentation").permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(ressource -> ressource
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(convertisseurJwt()))
                        .authenticationEntryPoint(erreurs)
                        .accessDeniedHandler(erreurs))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(erreurs)
                        .accessDeniedHandler(erreurs));
        return http.build();
    }

    @Bean
    JwtAuthenticationConverter convertisseurJwt() {
        JwtGrantedAuthoritiesConverter autorites = new JwtGrantedAuthoritiesConverter();
        autorites.setAuthoritiesClaimName("roles");
        autorites.setAuthorityPrefix("ROLE_");
        JwtAuthenticationConverter convertisseur = new JwtAuthenticationConverter();
        convertisseur.setJwtGrantedAuthoritiesConverter(autorites);
        return convertisseur;
    }
}