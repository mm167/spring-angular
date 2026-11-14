import { HttpErrorResponse } from '@angular/common/http';

import { Erreur } from '../api';

export function messageErreur(erreur: unknown): string {
  if (erreur instanceof HttpErrorResponse) {
    if (erreur.status === 0) {
      return 'Serveur injoignable.';
    }
    const corps = erreur.error as Partial<Erreur> | null;
    if (corps?.message) {
      return corps.message;
    }
    return `Erreur ${erreur.status}.`;
  }
  return 'Erreur inattendue.';
}
