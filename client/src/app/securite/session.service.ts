import { computed, Service, signal } from '@angular/core';

import { Jeton } from '../api';

export const CLE_SESSION = 'session';

interface Session {
  identifiant: string;
  jeton: Jeton;
}

function lireSession(): Session | null {
  try {
    const brut = sessionStorage.getItem(CLE_SESSION);
    return brut ? (JSON.parse(brut) as Session) : null;
  } catch {
    return null;
  }
}

@Service()
export class SessionService {
  private readonly session = signal<Session | null>(lireSession());

  readonly connecte = computed(() => this.session() !== null);
  readonly identifiant = computed(() => this.session()?.identifiant ?? '');
  readonly roles = computed(() => this.session()?.jeton.roles ?? []);
  readonly jetonAcces = computed(() => this.session()?.jeton.jetonAcces ?? null);
  readonly jetonRafraichissement = computed(() => this.session()?.jeton.jetonRafraichissement ?? null);
  readonly estGestionnaire = computed(() => this.roles().includes('GESTIONNAIRE'));

  ouvrir(identifiant: string, jeton: Jeton): void {
    const session: Session = { identifiant, jeton };
    this.session.set(session);
    sessionStorage.setItem(CLE_SESSION, JSON.stringify(session));
  }

  fermer(): void {
    this.session.set(null);
    sessionStorage.removeItem(CLE_SESSION);
  }

  remplacerJeton(jeton: Jeton): void {
    const actuelle = this.session();
    if (actuelle) {
      this.ouvrir(actuelle.identifiant, jeton);
    }
  }
}
