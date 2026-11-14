package fr.mm.formation.location.rest;

import java.util.List;

import fr.mm.formation.location.contrat.api.ReservationsApi;
import fr.mm.formation.location.contrat.modele.NouvelleReservation;
import fr.mm.formation.location.contrat.modele.Reservation;
import fr.mm.formation.location.depot.ReservationDepot;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReservationController implements ReservationsApi {

    private final ReservationDepot depot;

    public ReservationController(ReservationDepot depot) {
        this.depot = depot;
    }

    @Override
    public ResponseEntity<List<Reservation>> listerReservations() {
        return ResponseEntity.ok(depot.lister());
    }

    @Override
    public ResponseEntity<Reservation> creerReservation(NouvelleReservation nouvelleReservation) {
        return ResponseEntity.status(HttpStatus.CREATED).body(depot.creer(nouvelleReservation));
    }

    @Override
    public ResponseEntity<Void> annulerReservation(Long id) {
        if (depot.supprimer(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}