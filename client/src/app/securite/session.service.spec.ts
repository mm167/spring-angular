import { TestBed } from '@angular/core/testing';

import { Jeton } from '../api';
import { CLE_SESSION, SessionService } from './session.service';

const jetonClient: Jeton = { jetonAcces: 'a.b.c', expireDans: 900, roles: ['CLIENT'] };
const jetonGestion: Jeton = { jetonAcces: 'd.e.f', expireDans: 900, roles: ['GESTIONNAIRE'] };

describe('SessionService', () => {
  beforeEach(() => {
    sessionStorage.clear();
  });

  afterEach(() => {
    sessionStorage.clear();
  });

  it('ouvrir conserve la session et expose les rôles', () => {
    const service = TestBed.inject(SessionService);

    service.ouvrir('durand', jetonClient);

    expect(service.connecte()).toBe(true);
    expect(service.identifiant()).toBe('durand');
    expect(service.estGestionnaire()).toBe(false);
    expect(service.jetonAcces()).toBe('a.b.c');
    expect(sessionStorage.getItem(CLE_SESSION)).toContain('a.b.c');
  });

  it('fermer efface la session', () => {
    const service = TestBed.inject(SessionService);
    service.ouvrir('durand', jetonClient);

    service.fermer();

    expect(service.connecte()).toBe(false);
    expect(service.jetonAcces()).toBeNull();
    expect(sessionStorage.getItem(CLE_SESSION)).toBeNull();
  });

  it('relit la session conservée au démarrage', () => {
    sessionStorage.setItem(
      CLE_SESSION,
      JSON.stringify({ identifiant: 'gestion', jeton: jetonGestion }),
    );

    const service = TestBed.inject(SessionService);

    expect(service.connecte()).toBe(true);
    expect(service.estGestionnaire()).toBe(true);
  });
});
