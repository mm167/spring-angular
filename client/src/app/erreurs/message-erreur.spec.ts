import { HttpErrorResponse } from '@angular/common/http';

import { messageErreur } from './message-erreur';

describe('messageErreur', () => {
  it('retourne le message du corps Erreur', () => {
    const erreur = new HttpErrorResponse({
      status: 409,
      error: { status: 409, message: 'Période déjà réservée.' },
    });

    expect(messageErreur(erreur)).toBe('Période déjà réservée.');
  });

  it('signale un serveur injoignable quand le statut vaut 0', () => {
    expect(messageErreur(new HttpErrorResponse({ status: 0 }))).toBe('Serveur injoignable.');
  });

  it('retourne le code quand le corps est absent', () => {
    expect(messageErreur(new HttpErrorResponse({ status: 500 }))).toBe('Erreur 500.');
  });

  it('retourne un message générique pour une erreur inconnue', () => {
    expect(messageErreur(new Error('inconnue'))).toBe('Erreur inattendue.');
  });
});
