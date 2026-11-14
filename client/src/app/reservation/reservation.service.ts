import { HttpClient } from '@angular/common/http';
import { inject, Service } from '@angular/core';
import { Observable } from 'rxjs';

import { NouvelleReservation, Reservation } from '../api';

const URL_RESERVATIONS = 'http://localhost:8080/api/reservations';

@Service()
export class ReservationService {
  private readonly http = inject(HttpClient);

  lister(): Observable<Reservation[]> {
    return this.http.get<Reservation[]>(URL_RESERVATIONS);
  }

  creer(nouvelle: NouvelleReservation): Observable<Reservation> {
    return this.http.post<Reservation>(URL_RESERVATIONS, nouvelle);
  }

  annuler(id: number): Observable<void> {
    return this.http.delete<void>(`${URL_RESERVATIONS}/${id}`);
  }
}
