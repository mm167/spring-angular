package fr.mm.formation.location.rest;

import java.util.List;

import fr.mm.formation.location.contrat.api.MaterielsApi;
import fr.mm.formation.location.contrat.modele.Materiel;
import fr.mm.formation.location.contrat.modele.NouveauMateriel;
import fr.mm.formation.location.depot.MaterielDepot;
import fr.mm.formation.location.erreurs.MaterielIntrouvableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MaterielController implements MaterielsApi {

    private final MaterielDepot depot;

    public MaterielController(MaterielDepot depot) {
        this.depot = depot;
    }

    @Override
    public ResponseEntity<List<Materiel>> listerMateriels() {
        return ResponseEntity.ok(depot.lister());
    }

    @Override
    public ResponseEntity<Materiel> creerMateriel(NouveauMateriel nouveauMateriel) {
        return ResponseEntity.status(HttpStatus.CREATED).body(depot.creer(nouveauMateriel));
    }

    @Override
    public ResponseEntity<Materiel> consulterMateriel(Long id) {
        return depot.trouver(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new MaterielIntrouvableException(id));
    }
}