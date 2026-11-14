import { HttpErrorResponse } from '@angular/common/http';
import { signal } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { of, Subject, throwError } from 'rxjs';

import { Reservation } from '../../api';
import { Evenement, ReservationsEvenementsService } from '../../websocket/reservations-evenements.service';
import { ReservationService } from '../reservation.service';
import { ListeReservations } from './liste-reservations';

describe('ListeReservations', () => {
  const reservation: Reservation = {
    id: 1,
    materielId: 1,
    client: 'Entreprise Durand',
    dateDebut: '2026-12-01',
    dateFin: '2026-12-05',
  };

  const lister = vi.fn();
  const annuler = vi.fn();
  let evenements$: Subject<Evenement>;

  beforeEach(() => {
    lister.mockReset();
    annuler.mockReset();
    lister.mockReturnValue(of([reservation]));
    evenements$ = new Subject<Evenement>();

    TestBed.configureTestingModule({
      providers: [
        { provide: ReservationService, useValue: { lister, annuler, creer: vi.fn() } },
        {
          provide: ReservationsEvenementsService,
          useValue: { evenements$, connecte: signal(true) },
        },
      ],
    });
  });

  function texte(fixture: { nativeElement: unknown }): string {
    return (fixture.nativeElement as HTMLElement).textContent ?? '';
  }

  it('affiche les réservations et l’état du canal', async () => {
    const fixture = TestBed.createComponent(ListeReservations);
    await fixture.whenStable();

    expect(texte(fixture)).toContain('Entreprise Durand');
    expect(texte(fixture)).toContain('Canal : connecté');
  });

  it('recharge la liste à chaque événement du canal', async () => {
    const fixture = TestBed.createComponent(ListeReservations);
    await fixture.whenStable();
    expect(lister).toHaveBeenCalledTimes(1);

    evenements$.next({ type: 'RESERVATION_ANNULEE', contenu: { id: 1 } });
    await fixture.whenStable();

    expect(lister).toHaveBeenCalledTimes(2);
    expect(texte(fixture)).toContain('Dernier événement : RESERVATION_ANNULEE');
  });

  it('affiche le message de l’API quand l’annulation échoue', async () => {
    annuler.mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            status: 404,
            error: { status: 404, message: 'Réservation introuvable.' },
          }),
      ),
    );
    const fixture = TestBed.createComponent(ListeReservations);
    await fixture.whenStable();

    const bouton = (fixture.nativeElement as HTMLElement).querySelector('table button');
    (bouton as HTMLButtonElement).click();
    await fixture.whenStable();

    expect(annuler).toHaveBeenCalledWith(1);
    expect(texte(fixture)).toContain('Réservation introuvable.');
  });
});
