import { Routes } from '@angular/router';

import { ListeMateriels } from './materiel/liste-materiels/liste-materiels';
import { ListeReservations } from './reservation/liste-reservations/liste-reservations';
import { Connexion } from './securite/connexion/connexion';
import { connecteGuard } from './securite/connecte.guard';

export const routes: Routes = [
  { path: 'materiels', component: ListeMateriels },
  { path: 'reservations', component: ListeReservations, canActivate: [connecteGuard] },
  { path: 'connexion', component: Connexion },
  { path: '', pathMatch: 'full', redirectTo: 'materiels' },
  { path: '**', redirectTo: 'materiels' },
];
