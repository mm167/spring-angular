package fr.mm.formation.location.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.List;
import java.util.Optional;

import fr.mm.formation.location.contrat.modele.Categorie;
import fr.mm.formation.location.contrat.modele.Materiel;
import fr.mm.formation.location.depot.MaterielDepot;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@WebMvcTest(MaterielController.class)
class MaterielControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private MaterielDepot depot;

    @Test
    void listerRetourneLesMateriels() {
        given(depot.lister()).willReturn(List.of(materiel(1L, "Mini-pelle")));

        assertThat(mvc.get().uri("/api/materiels"))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$[0].nom").isEqualTo("Mini-pelle");
    }

    @Test
    void consulterUnMaterielInconnuRetourne404() {
        given(depot.trouver(99L)).willReturn(Optional.empty());

        assertThat(mvc.get().uri("/api/materiels/{id}", 99))
                .hasStatus(HttpStatus.NOT_FOUND)
                .bodyJson()
                .extractingPath("$.status").isEqualTo(404);
    }

    @Test
    void creerAvecNomVideRetourne400() {
        assertThat(mvc.post().uri("/api/materiels")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nom\":\"\",\"categorie\":\"NACELLE\",\"tarifJournalier\":260.0}"))
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson()
                .extractingPath("$.status").isEqualTo(400);
    }

    private static Materiel materiel(Long id, String nom) {
        Materiel materiel = new Materiel();
        materiel.setId(id);
        materiel.setNom(nom);
        materiel.setCategorie(Categorie.MINI_PELLE);
        materiel.setTarifJournalier(180.0);
        return materiel;
    }
}