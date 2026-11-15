import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

import { SessionService } from './session.service';

const URL_API = 'http://localhost:8080/api/';
const CHEMIN_LOGIN = '/api/auth/login';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const session = inject(SessionService);
  const router = inject(Router);

  if (!req.url.startsWith(URL_API) || req.url.endsWith(CHEMIN_LOGIN)) {
    return next(req);
  }

  const jeton = session.jetonAcces();
  const requete = jeton ? req.clone({ setHeaders: { Authorization: `Bearer ${jeton}` } }) : req;

  return next(requete).pipe(
    catchError((erreur: HttpErrorResponse) => {
      if (erreur.status === 401) {
        session.fermer();
        void router.navigate(['/connexion'], { queryParams: { retour: router.url } });
      }
      return throwError(() => erreur);
    }),
  );
};
