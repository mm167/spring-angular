package fr.mm.formation.location.rest;

import java.util.List;
import java.util.Map;

import fr.mm.formation.location.contrat.api.ReservationsApi;
import fr.mm.formation.location.contrat.modele.NouvelleReservation;
import fr.mm.formation.location.contrat.modele.Reservation;
import fr.mm.formation.location.depot.MaterielDepot;
import fr.mm.formation.location.depot.ReservationDepot;
import fr.mm.formation.location.erreurs.ChevauchementException;
import fr.mm.formation.location.erreurs.MaterielIntrouvableException;
import fr.mm.formation.location.erreurs.ReservationIntrouvableException;
import fr.mm.formation.location.websocket.ReservationsWebSocketHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReservationController implements ReservationsApi {

    private final ReservationDepot depot;
    private final MaterielDepot materielDepot;
    private final ReservationsWebSocketHandler notifications;

    public ReservationController(ReservationDepot depot, MaterielDepot materielDepot,
                                 ReservationsWebSocketHandler notifications) {
        this.depot = depot;
        this.materielDepot = materielDepot;
        this.notifications = notifications;
    }

    @Override
    public ResponseEntity<List<Reservation>> listerReservations() {
        return ResponseEntity.ok(depot.lister());
    }

    @Override
    public ResponseEntity<Reservation> creerReservation(NouvelleReservation nouvelleReservation) {
        Long materielId = nouvelleReservation.getMaterielId();
        materielDepot.trouver(materielId)
                .orElseThrow(() -> new MaterielIntrouvableException(materielId));
        verifierDisponibilite(nouvelleReservation);
        Reservation creee = depot.creer(nouvelleReservation);
        notifications.diffuser("RESERVATION_CREEE", creee);
        return ResponseEntity.status(HttpStatus.CREATED).body(creee);
    }

    @Override
    public ResponseEntity<Void> annulerReservation(Long id) {
        if (!depot.supprimer(id)) {
            throw new ReservationIntrouvableException(id);
        }
        notifications.diffuser("RESERVATION_ANNULEE", Map.of("id", id));
        return ResponseEntity.noContent().build();
    }

    private void verifierDisponibilite(NouvelleReservation demande) {
        boolean chevauchement = depot.lister().stream()
                .filter(reservation -> reservation.getMaterielId().equals(demande.getMaterielId()))
                .anyMatch(reservation -> demande.getDateDebut().isBefore(reservation.getDateFin())
                        && reservation.getDateDebut().isBefore(demande.getDateFin()));
        if (chevauchement) {
            throw new ChevauchementException(demande.getMaterielId());
        }
    }
}