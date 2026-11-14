package fr.mm.formation.location.securite;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtIssuerValidator;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.JwtTypeValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

@Configuration
@EnableConfigurationProperties(SecuriteProprietes.class)
public class JwtConfig {

    @Bean
    JwtEncoder jwtEncoder(SecuriteProprietes proprietes) {
        return new NimbusJwtEncoder(new ImmutableSecret<SecurityContext>(cle(proprietes)));
    }

    @Bean
    JwtDecoder jwtDecoder(SecuriteProprietes proprietes) {
        NimbusJwtDecoder decodeur = NimbusJwtDecoder.withSecretKey(cle(proprietes))
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
        decodeur.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                new JwtTimestampValidator(Duration.ZERO),
                new JwtIssuerValidator(JetonService.EMETTEUR),
                JwtTypeValidator.jwt()));
        return decodeur;
    }

    private static SecretKey cle(SecuriteProprietes proprietes) {
        return new SecretKeySpec(proprietes.secret().getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }
}