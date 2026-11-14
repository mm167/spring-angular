package fr.mm.formation.location.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import fr.mm.formation.location.contrat.modele.Materiel;
import fr.mm.formation.location.contrat.modele.NouvelleReservation;
import fr.mm.formation.location.contrat.modele.Reservation;
import fr.mm.formation.location.depot.MaterielDepot;
import fr.mm.formation.location.depot.ReservationDepot;
import fr.mm.formation.location.websocket.ReservationsWebSocketHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

    private static final String DEMANDE = "{\"materielId\":1,\"client\":\"Entreprise Martin\","
            + "\"dateDebut\":\"2026-12-03\",\"dateFin\":\"2026-12-07\"}";

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private ReservationDepot depot;

    @MockitoBean
    private MaterielDepot materielDepot;

    @MockitoBean
    private ReservationsWebSocketHandler notifications;

    @Test
    void creerRetourne201EtDiffuseUnEvenement() {
        given(materielDepot.trouver(1L)).willReturn(Optional.of(new Materiel()));
        given(depot.lister()).willReturn(List.of());
        given(depot.creer(any(NouvelleReservation.class)))
                .willReturn(reservation(2L, LocalDate.of(2026, 12, 3), LocalDate.of(2026, 12, 7)));

        assertThat(mvc.post().uri("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(DEMANDE))
                .hasStatus(HttpStatus.CREATED)
                .bodyJson()
                .extractingPath("$.id").isEqualTo(2);

        verify(notifications).diffuser(eq("RESERVATION_CREEE"), any());
    }

    @Test
    void creerAvecChevauchementRetourne409() {
        given(materielDepot.trouver(1L)).willReturn(Optional.of(new Materiel()));
        given(depot.lister()).willReturn(List.of(
                reservation(1L, LocalDate.of(2026, 12, 1), LocalDate.of(2026, 12, 5))));

        assertThat(mvc.post().uri("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(DEMANDE))
                .hasStatus(HttpStatus.CONFLICT)
                .bodyJson()
                .extractingPath("$.status").isEqualTo(409);

        verify(depot, never()).creer(any(NouvelleReservation.class));
        verifyNoInteractions(notifications);
    }

    @Test
    void annulerUneReservationInconnueRetourne404() {
        given(depot.supprimer(99L)).willReturn(false);

        assertThat(mvc.delete().uri("/api/reservations/{id}", 99))
                .hasStatus(HttpStatus.NOT_FOUND);

        verifyNoInteractions(notifications);
    }

    private static Reservation reservation(Long id, LocalDate debut, LocalDate fin) {
        Reservation reservation = new Reservation();
        reservation.setId(id);
        reservation.setMaterielId(1L);
        reservation.setClient("Entreprise Durand");
        reservation.setDateDebut(debut);
        reservation.setDateFin(fin);
        return reservation;
    }
}