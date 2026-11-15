import { HttpErrorResponse, HttpInterceptorFn, HttpRequest } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, switchMap, throwError } from 'rxjs';

import { RafraichissementService } from './rafraichissement.service';
import { SessionService } from './session.service';

const URL_API = 'http://localhost:8080/api/';
const CHEMINS_SANS_JETON = ['/api/auth/login', '/api/auth/rafraichir'];

function avecJeton(req: HttpRequest<unknown>, jeton: string | null): HttpRequest<unknown> {
  return jeton ? req.clone({ setHeaders: { Authorization: `Bearer ${jeton}` } }) : req;
}

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const session = inject(SessionService);
  const rafraichissement = inject(RafraichissementService);
  const router = inject(Router);

  if (!req.url.startsWith(URL_API) || CHEMINS_SANS_JETON.some((chemin) => req.url.endsWith(chemin))) {
    return next(req);
  }

  const deconnecter = () => {
    session.fermer();
    void router.navigate(['/connexion'], { queryParams: { retour: router.url } });
  };

  return next(avecJeton(req, session.jetonAcces())).pipe(
    catchError((erreur: HttpErrorResponse) => {
      if (erreur.status !== 401) {
        return throwError(() => erreur);
      }
      if (!session.jetonRafraichissement()) {
        deconnecter();
        return throwError(() => erreur);
      }
      return rafraichissement.rafraichir().pipe(
        catchError((echec) => {
          deconnecter();
          return throwError(() => echec);
        }),
        switchMap((jeton) => next(avecJeton(req, jeton.jetonAcces))),
      );
    }),
  );
};
