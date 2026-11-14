import { AsyncPipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { BehaviorSubject, switchMap } from 'rxjs';

import { NouvelleReservation } from '../../api';
import { messageErreur } from '../../erreurs/message-erreur';
import { ReservationService } from '../reservation.service';

@Component({
  selector: 'app-liste-reservations',
  imports: [AsyncPipe],
  templateUrl: './liste-reservations.html',
})
export class ListeReservations {
  private readonly service = inject(ReservationService);
  private readonly rechargement$ = new BehaviorSubject<void>(undefined);

  protected readonly reservations$ = this.rechargement$.pipe(
    switchMap(() => this.service.lister()),
  );

  protected readonly message = signal('');

  protected readonly libre: NouvelleReservation = {
    materielId: 2,
    client: 'Entreprise Martin',
    dateDebut: '2026-12-10',
    dateFin: '2026-12-12',
  };
  protected readonly chevauchement: NouvelleReservation = {
    materielId: 1,
    client: 'Entreprise Martin',
    dateDebut: '2026-12-03',
    dateFin: '2026-12-07',
  };
  protected readonly materielInconnu: NouvelleReservation = {
    materielId: 99,
    client: 'Entreprise Martin',
    dateDebut: '2026-12-10',
    dateFin: '2026-12-12',
  };
  protected readonly clientVide: NouvelleReservation = {
    materielId: 2,
    client: '',
    dateDebut: '2026-12-10',
    dateFin: '2026-12-12',
  };

  protected reserver(demande: NouvelleReservation): void {
    this.service.creer(demande).subscribe({
      next: () => {
        this.message.set('Réservation créée.');
        this.rechargement$.next();
      },
      error: (erreur) => this.message.set(messageErreur(erreur)),
    });
  }

  protected annuler(id: number): void {
    this.service.annuler(id).subscribe({
      next: () => {
        this.message.set('Réservation annulée.');
        this.rechargement$.next();
      },
      error: (erreur) => this.message.set(messageErreur(erreur)),
    });
  }
}
