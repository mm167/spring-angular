package fr.mm.formation.location.securite;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("location.securite")
public record SecuriteProprietes(String secret, Duration dureeJeton, Duration dureeRafraichissement) {
}