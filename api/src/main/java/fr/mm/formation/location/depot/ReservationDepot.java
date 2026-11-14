package fr.mm.formation.location.depot;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

import fr.mm.formation.location.contrat.modele.NouvelleReservation;
import fr.mm.formation.location.contrat.modele.Reservation;
import org.springframework.stereotype.Repository;

@Repository
public class ReservationDepot {

    private final Map<Long, Reservation> reservations = new ConcurrentSkipListMap<>();
    private final AtomicLong sequence = new AtomicLong();

    public ReservationDepot() {
        NouvelleReservation depart = new NouvelleReservation();
        depart.setMaterielId(1L);
        depart.setClient("Entreprise Durand");
        depart.setDateDebut(LocalDate.of(2026, 12, 1));
        depart.setDateFin(LocalDate.of(2026, 12, 5));
        creer(depart);
    }

    public List<Reservation> lister() {
        return List.copyOf(reservations.values());
    }

    public Reservation creer(NouvelleReservation nouvelle) {
        Reservation reservation = new Reservation();
        reservation.setId(sequence.incrementAndGet());
        reservation.setMaterielId(nouvelle.getMaterielId());
        reservation.setClient(nouvelle.getClient());
        reservation.setDateDebut(nouvelle.getDateDebut());
        reservation.setDateFin(nouvelle.getDateFin());
        reservations.put(reservation.getId(), reservation);
        return reservation;
    }

    public boolean supprimer(Long id) {
        return reservations.remove(id) != null;
    }
}