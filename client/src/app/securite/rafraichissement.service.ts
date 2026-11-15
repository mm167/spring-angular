import { inject, Service } from '@angular/core';
import { finalize, Observable, shareReplay, tap, throwError } from 'rxjs';

import { AuthentificationService, Jeton } from '../api';
import { SessionService } from './session.service';

@Service()
export class RafraichissementService {
  private readonly authentification = inject(AuthentificationService);
  private readonly session = inject(SessionService);
  private enCours$: Observable<Jeton> | null = null;

  rafraichir(): Observable<Jeton> {
    const jetonRafraichissement = this.session.jetonRafraichissement();
    if (!jetonRafraichissement) {
      return throwError(() => new Error('Aucun jeton de rafraîchissement.'));
    }
    if (!this.enCours$) {
      this.enCours$ = this.authentification.rafraichir({ jetonRafraichissement }).pipe(
        tap((jeton) => this.session.remplacerJeton(jeton)),
        finalize(() => (this.enCours$ = null)),
        shareReplay({ bufferSize: 1, refCount: false }),
      );
    }
    return this.enCours$;
  }
}
