package fr.mm.formation.location.appels;

import java.util.List;

import fr.mm.formation.location.contrat.modele.Materiel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/outils")
public class OutilsController {

    private final MaterielClient client;

    public OutilsController(MaterielClient client) {
        this.client = client;
    }

    @GetMapping("/materiels")
    public List<Materiel> lister() {
        return client.lister();
    }

    @GetMapping("/materiels/{id}")
    public Materiel trouver(@PathVariable Long id) {
        return client.trouver(id);
    }
}