package fr.mm.formation.location.reactif;

import java.time.Duration;

import fr.mm.formation.location.contrat.modele.Materiel;
import fr.mm.formation.location.depot.MaterielDepot;
import fr.mm.formation.location.erreurs.MaterielIntrouvableException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/reactif/materiels")
public class MaterielReactifController {

    private final MaterielDepot depot;

    public MaterielReactifController(MaterielDepot depot) {
        this.depot = depot;
    }

    @GetMapping("/{id}")
    public Mono<Materiel> consulter(@PathVariable Long id) {
        return Mono.justOrEmpty(depot.trouver(id))
                .switchIfEmpty(Mono.error(() -> new MaterielIntrouvableException(id)));
    }

    @GetMapping
    public Flux<Materiel> lister() {
        return Flux.fromIterable(depot.lister());
    }

    @GetMapping(path = "/flux", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Materiel> listerEnFlux() {
        return Flux.fromIterable(depot.lister())
                .delayElements(Duration.ofSeconds(1))
                .index()
                .map(indexe -> {
                    if (indexe.getT1() == 1L) {
                        throw new IllegalStateException("Panne simulée");
                    }
                    return indexe.getT2();
                });
    }
}