import { Service, signal } from '@angular/core';
import { Observable, repeat, retry, timer } from 'rxjs';
import { webSocket } from 'rxjs/webSocket';

import { Reservation } from '../api';

export type Evenement =
  | { type: 'RESERVATION_CREEE'; contenu: Reservation }
  | { type: 'RESERVATION_ANNULEE'; contenu: { id: number } };

const URL_CANAL = 'ws://localhost:8080/ws/reservations';
const DELAI_RECONNEXION = 3000;

@Service()
export class ReservationsEvenementsService {
  readonly connecte = signal(false);

  readonly evenements$: Observable<Evenement> = webSocket<Evenement>({
    url: URL_CANAL,
    openObserver: { next: () => this.connecte.set(true) },
    closeObserver: { next: () => this.connecte.set(false) },
  }).pipe(
    retry({ delay: () => timer(DELAI_RECONNEXION) }),
    repeat({ delay: () => timer(DELAI_RECONNEXION) }),
  );
}
