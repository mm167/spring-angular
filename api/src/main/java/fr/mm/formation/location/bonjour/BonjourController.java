package fr.mm.formation.location.bonjour;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BonjourController {

    @GetMapping("/api/bonjour")
    public BonjourReponse direBonjour() {
        return new BonjourReponse("Bonjour depuis l'API Spring Boot !");
    }

}