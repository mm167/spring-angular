import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { SessionService } from './session.service';

export const connecteGuard: CanActivateFn = (route, state) => {
  const session = inject(SessionService);
  const router = inject(Router);

  if (session.connecte()) {
    return true;
  }
  return router.createUrlTree(['/connexion'], { queryParams: { retour: state.url } });
};
