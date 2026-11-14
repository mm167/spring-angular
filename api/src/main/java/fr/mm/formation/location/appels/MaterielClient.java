package fr.mm.formation.location.appels;

import java.util.List;

import fr.mm.formation.location.contrat.modele.Materiel;
import fr.mm.formation.location.erreurs.MaterielIntrouvableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class MaterielClient {

    private final RestClient restClient;

    public MaterielClient(RestClient.Builder builder,
                          @Value("${location.api.url:http://localhost:8080}") String urlApi) {
        this.restClient = builder.baseUrl(urlApi).build();
    }

    public List<Materiel> lister() {
        return restClient.get()
                .uri("/api/materiels")
                .retrieve()
                .body(new ParameterizedTypeReference<List<Materiel>>() { });
    }

    public Materiel trouver(Long id) {
        return restClient.get()
                .uri("/api/materiels/{id}", id)
                .retrieve()
                .onStatus(statut -> statut.value() == 404, (requete, reponse) -> {
                    throw new MaterielIntrouvableException(id);
                })
                .body(Materiel.class);
    }
}