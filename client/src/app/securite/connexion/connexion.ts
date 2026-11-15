import { Component, inject, signal } from '@angular/core';
import { form, FormField, required } from '@angular/forms/signals';
import { ActivatedRoute, Router } from '@angular/router';

import { AuthentificationService } from '../../api';
import { messageErreur } from '../../erreurs/message-erreur';
import { SessionService } from '../session.service';

@Component({
  selector: 'app-connexion',
  imports: [FormField],
  templateUrl: './connexion.html',
})
export class Connexion {
  private readonly authentification = inject(AuthentificationService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  protected readonly session = inject(SessionService);

  private readonly saisie = signal({ identifiant: '', motDePasse: '' });
  protected readonly formulaire = form(this.saisie, (champs) => {
    required(champs.identifiant);
    required(champs.motDePasse);
  });

  protected readonly message = signal('');

  protected seConnecter(evenement: Event): void {
    evenement.preventDefault();
    if (this.formulaire().invalid()) {
      this.message.set('Renseignez l’identifiant et le mot de passe.');
      return;
    }
    const { identifiant, motDePasse } = this.saisie();
    this.authentification.seConnecter({ identifiant, motDePasse }).subscribe({
      next: (jeton) => {
        this.session.ouvrir(identifiant, jeton);
        this.saisie.set({ identifiant: '', motDePasse: '' });
        this.message.set('');
        void this.router.navigateByUrl(this.destination());
      },
      error: (erreur) => this.message.set(messageErreur(erreur)),
    });
  }

  private destination(): string {
    const retour = this.route.snapshot.queryParamMap.get('retour');
    return retour && retour.startsWith('/') && !retour.startsWith('//') ? retour : '/reservations';
  }
}
