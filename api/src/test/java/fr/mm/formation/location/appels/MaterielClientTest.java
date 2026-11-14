package fr.mm.formation.location.appels;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import fr.mm.formation.location.contrat.modele.Materiel;
import fr.mm.formation.location.erreurs.MaterielIntrouvableException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

@RestClientTest(MaterielClient.class)
class MaterielClientTest {

    @Autowired
    private MaterielClient client;

    @Autowired
    private MockRestServiceServer serveur;

    @Test
    void trouverLitLeMateriel() {
        serveur.expect(requestTo("http://localhost:8080/api/materiels/1"))
                .andRespond(withSuccess(
                        "{\"id\":1,\"nom\":\"Mini-pelle\",\"categorie\":\"MINI_PELLE\",\"tarifJournalier\":180.0}",
                        MediaType.APPLICATION_JSON));

        Materiel materiel = client.trouver(1L);

        assertThat(materiel.getNom()).isEqualTo("Mini-pelle");
        serveur.verify();
    }

    @Test
    void trouverUnMaterielInconnuLeveUneExceptionMetier() {
        serveur.expect(requestTo("http://localhost:8080/api/materiels/99"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThatThrownBy(() -> client.trouver(99L))
                .isInstanceOf(MaterielIntrouvableException.class);
    }
}