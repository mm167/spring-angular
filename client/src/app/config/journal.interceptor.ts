import { HttpErrorResponse, HttpInterceptorFn, HttpResponse } from '@angular/common/http';
import { tap } from 'rxjs';

export const journalInterceptor: HttpInterceptorFn = (req, next) => {
  const debut = performance.now();
  const duree = () => Math.round(performance.now() - debut);

  return next(req).pipe(
    tap({
      next: (evenement) => {
        if (evenement instanceof HttpResponse) {
          console.log(`${req.method} ${req.urlWithParams} -> ${evenement.status} (${duree()} ms)`);
        }
      },
      error: (erreur: HttpErrorResponse) => {
        console.log(`${req.method} ${req.urlWithParams} -> ERREUR ${erreur.status} (${duree()} ms)`);
      },
    }),
  );
};
